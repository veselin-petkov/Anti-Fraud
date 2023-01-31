package antifraud.service;

import antifraud.configuration.TransactionProperty;
import antifraud.model.Card;
import antifraud.model.Transaction;
import antifraud.model.TransactionFeedback;
import antifraud.model.enums.TransactionResult;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.repository.CardRepository;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import antifraud.repository.TransactionRepository;
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
    @Autowired
    TransactionProperty transactionProperty;

    public TransactionResponse processTransaction(TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse = new TransactionResponse();
        List<String> info = new ArrayList<>();
        Transaction transaction = transactionRequestToTransaction(transactionRequest);
        boolean manual = false;

        Card card = cardRepository.findByNumber(transactionRequest.getNumber());

        int maxAllowed = transactionProperty.getInitialMaxAllowed();
        int maxManual = transactionProperty.getInitialMaxManual();

        if (card != null) {
            maxAllowed = card.getMaxAllowed();
            maxManual = card.getMaxManual();
        } else {
            card = new Card();
            card.setNumber(transactionRequest.getNumber());
            card.setMaxAllowed(maxAllowed);
            card.setMaxManual(maxManual);
            cardRepository.save(card);
        }
        if (transactionRequest.getAmount() <= maxAllowed) {
            transactionResponse.setResult(ALLOWED);
        } else if (transactionRequest.getAmount() <= maxManual) {
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

    /**
     * @param ip
     * @return true or false if the ip is in the blacklist
     */
    private boolean checkForSuspiciousIp(String ip) {
        return suspiciousIpRepository.existsByIp(ip);
    }

    public Transaction transactionFeedback(TransactionFeedback transactionFeedback) {
        Transaction transaction = transactionRepository.findById(transactionFeedback.getTransactionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Card card = cardRepository.findByNumber(transaction.getNumber());
        int maxAllowed = card.getMaxAllowed();
        int maxManual = card.getMaxManual();

        if (transactionFeedback.getFeedback() == transaction.getResult()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!transaction.getFeedback().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else if (transactionFeedback.getFeedback() == ALLOWED) {
            maxAllowed = increaseMaxAllowed(transaction, maxAllowed);
            if (transaction.getResult() == PROHIBITED) {
                maxManual = increaseMaxManual(transaction, maxManual);
                cardRepository.updateMaxAllowedAndMaxManualByNumber(maxAllowed, maxManual, transaction.getNumber());
            } else {
                cardRepository.updateMaxAllowedByNumber(maxAllowed, transaction.getNumber());
            }
            transaction.setFeedback(transactionFeedback.getFeedback());
        } else if (transactionFeedback.getFeedback() == MANUAL_PROCESSING) {
            if (transaction.getResult() == ALLOWED) {
                maxAllowed = decreaseMaxAllowed(transaction, maxAllowed);
                cardRepository.updateMaxAllowedByNumber(maxAllowed, transaction.getNumber());
            } else {
                maxManual = increaseMaxManual(transaction, maxManual);
                cardRepository.updateMaxManualByNumber(maxManual, transaction.getNumber());
            }
            transaction.setFeedback(transactionFeedback.getFeedback());
        } else if (transactionFeedback.getFeedback() == PROHIBITED) {
            maxManual = decreaseMaxManual(transaction, maxManual);
            if ((transaction.getResult() == MANUAL_PROCESSING)) {
                cardRepository.updateMaxManualByNumber(maxManual, transaction.getNumber());
            } else {
                maxAllowed = decreaseMaxAllowed(transaction, maxAllowed);
                cardRepository.updateMaxAllowedAndMaxManualByNumber(maxAllowed, maxManual, transaction.getNumber());
            }
            transaction.setFeedback(transactionFeedback.getFeedback());
        }
        transactionRepository.save(transaction);
        return transaction;
    }

    private int decreaseMaxManual(Transaction transaction, int maxManual) {
        return (int) Math.ceil((transactionProperty.getCurrentLimitModifier() * maxManual)
                - (transactionProperty.getValueFromTransactionModifier() * transaction.getAmount()));
    }

    private int decreaseMaxAllowed(Transaction transaction, int maxAllowed) {
        return (int) Math.ceil((transactionProperty.getCurrentLimitModifier() * maxAllowed)
                - (transactionProperty.getValueFromTransactionModifier() * transaction.getAmount()));
    }

    private int increaseMaxAllowed(Transaction transaction, int maxAllowed) {
        return (int) Math.ceil((transactionProperty.getCurrentLimitModifier() * maxAllowed)
                + (transactionProperty.getValueFromTransactionModifier() * transaction.getAmount()));
    }

    private int increaseMaxManual(Transaction transaction, int maxManual) {
        return (int) Math.ceil((transactionProperty.getCurrentLimitModifier() * maxManual)
                + (transactionProperty.getValueFromTransactionModifier() * transaction.getAmount()));
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
