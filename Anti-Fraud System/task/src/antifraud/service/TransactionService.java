package antifraud.service;

import antifraud.model.Card;
import antifraud.model.TransactionFeedback;
import antifraud.model.Transaction;
import antifraud.model.enums.TransactionResult;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.repository.CardRepository;
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
    @Autowired
    CardRepository cardRepository;

    public TransactionResponse processTransaction(TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse = new TransactionResponse();
        List<String> info = new ArrayList<>();
        Transaction transaction = transactionRequestToTransaction(transactionRequest);
        boolean manual = false;

        Card card = cardRepository.findByNumber(transactionRequest.getNumber());

        int max_ALLOWED = 200;
        int max_MANUAL = 1500;

        if (card != null) {
            max_ALLOWED = card.getMax_ALLOWED();
            max_MANUAL = card.getMax_MANUAL();
        } else {
            card = new Card();
            card.setNumber(transactionRequest.getNumber());
            card.setMax_ALLOWED(max_ALLOWED);
            card.setMax_MANUAL(max_MANUAL);
            cardRepository.save(card);
        }
        if (transactionRequest.getAmount() <= max_ALLOWED) {
            transactionResponse.setResult(ALLOWED);
        } else if (transactionRequest.getAmount() <= max_MANUAL) {
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

        Card card = cardRepository.findByNumber(transaction.getNumber());

        int max_ALLOWED = card.getMax_ALLOWED();
        int max_MANUAL = card.getMax_MANUAL();

        if (transactionFeedback.getFeedback() == transaction.getResult()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (!transaction.getFeedback().equals("")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else if (transactionFeedback.getFeedback() == ALLOWED) {
            transaction.setFeedback(transactionFeedback.getFeedback());
            max_ALLOWED = (int) Math.ceil((0.8 * max_ALLOWED) + (0.2 * transaction.getAmount()));
            cardRepository.updateMax_ALLOWEDByNumber(max_ALLOWED, transaction.getNumber());
            if (transaction.getResult() == PROHIBITED) {
                max_MANUAL = (int) Math.ceil((0.8 * max_MANUAL) + (0.2 * transaction.getAmount()));
                cardRepository.updateMax_MANUALByNumber(max_MANUAL, transaction.getNumber());
            }
        } else if (transactionFeedback.getFeedback() == MANUAL_PROCESSING) {
            transaction.setFeedback(transactionFeedback.getFeedback());
            if (transaction.getResult() == ALLOWED) {
                max_ALLOWED = (int) Math.ceil((0.8 * max_ALLOWED) - (0.2 * transaction.getAmount()));
                cardRepository.updateMax_ALLOWEDByNumber(max_ALLOWED, transaction.getNumber());
            } else {
                max_MANUAL = (int) Math.ceil((0.8 * max_MANUAL) + (0.2 * transaction.getAmount()));
                cardRepository.updateMax_MANUALByNumber(max_MANUAL, transaction.getNumber());
            }
        } else if (transactionFeedback.getFeedback() == PROHIBITED) {
            if (transaction.getResult() == ALLOWED) {
                transaction.setFeedback(transactionFeedback.getFeedback());
                max_ALLOWED = (int) Math.ceil((0.8 * max_ALLOWED) - (0.2 * transaction.getAmount()));
                cardRepository.updateMax_ALLOWEDByNumber(max_ALLOWED, transaction.getNumber());
            }
            transaction.setFeedback(transactionFeedback.getFeedback());
            max_MANUAL = (int) Math.ceil((0.8 * max_MANUAL) - (0.2 * transaction.getAmount()));
            cardRepository.updateMax_MANUALByNumber(max_MANUAL, transaction.getNumber());
        }
        return transaction;
    }

    public List<Transaction> listTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionById(String number) {
        List<Transaction> list = transactionRepository.findAllByNumber(number);
        if (list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return list;
        }
    }
}
