package antifraud.service;

import antifraud.model.TransactionFeedback;
import antifraud.model.Transaction;
import antifraud.model.enums.TransactionResult;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.repository.TransactionRepository;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static antifraud.mappers.ModelMapper.transactionRequestToTransaction;
import static antifraud.model.enums.TransactionResult.*;

@Slf4j
@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    StolenCardRepository stolenCardRepository;
    @Autowired
    SuspiciousIpRepository suspiciousIpRepository;

    public TransactionResponse processTransaction(TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse = new TransactionResponse();
        List<String> info = new ArrayList<>();
        Transaction transaction = transactionRequestToTransaction(transactionRequest);

        boolean manual = false;
        if (transactionRequest.getAmount() <= 200) {
            transactionResponse.setResult(ALLOWED);
        } else if (transactionRequest.getAmount() <= 1500) {
            transactionResponse.setResult(MANUAL_PROCESSING);
            info.add("amount");
            manual = true;
        } else {
            transactionResponse.setResult(PROHIBITED);
            info.add("amount");
        }
        if (checkForStolenCard(transactionRequest.getNumber())) {
            transactionResponse.setResult(PROHIBITED);
            info.add("card-number");
            if (manual) {
                info.remove("amount");
            }
        }
        if (checkForSuspiciousIp(transactionRequest.getIp())) {
            transactionResponse.setResult(PROHIBITED);
            info.add("ip");
            if (manual) {
                info.remove("amount");
            }
        }
        transactionRepository.save(transaction);
        List<Transaction> transactionHistory = transactionRepository.findByNumberAndDateBetween
                (transactionRequest.getNumber(), transactionRequest.getDate().minusHours(1), transactionRequest.getDate());

        long uniqueIps = transactionHistory.stream().map(Transaction::getIp).distinct().count();
        long uniqueRegions = transactionHistory.stream().map(Transaction::getRegion).distinct().count();

        if (checkNumberOf(uniqueRegions).equals(PROHIBITED)) {
            transactionResponse.setResult(PROHIBITED);
            info.add("region-correlation");
        } else if (checkNumberOf(uniqueRegions).equals(MANUAL_PROCESSING)) {
            transactionResponse.setResult(MANUAL_PROCESSING);
            info.add("region-correlation");
        }

        if (checkNumberOf(uniqueIps).equals(PROHIBITED)) {
            transactionResponse.setResult(PROHIBITED);
            info.add("ip-correlation");
        } else if (checkNumberOf(uniqueIps).equals(MANUAL_PROCESSING)) {
            transactionResponse.setResult(MANUAL_PROCESSING);
            info.add("ip-correlation");
        }

        if (info.isEmpty()) {
            transactionResponse.setInfo("none");
        } else {
            transactionResponse.setInfo(String.join(", ", info));
        }
        transaction.setResult(transactionResponse.getResult());
        transactionRepository.save(transaction);

        return transactionResponse;
    }

    private TransactionResult checkNumberOf(long nUniqueRequests) {
        if (nUniqueRequests <= 2) {
            return ALLOWED;
        }
        return nUniqueRequests == 3 ? MANUAL_PROCESSING :
                PROHIBITED;
    }

    private boolean checkForStolenCard(String number) {
        return stolenCardRepository.existsByNumber(number);
    }

    private boolean checkForSuspiciousIp(String ip) {
        return suspiciousIpRepository.existsByIp(ip);
    }

    public Transaction transactionFeedback(TransactionFeedback transactionFeedback) {
        Transaction transaction = transactionRepository.findById(transactionFeedback.getTransactionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (transaction.getFeedback() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else {
            transaction.setFeedback(transactionFeedback.getFeedback());
        }
        return transaction;
    }

    public List<Transaction> listTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionById(String number) {
        List<Transaction> list = transactionRepository.findAllByNumber(number);
        if (list.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }else {
            return list;
        }
    }
}
