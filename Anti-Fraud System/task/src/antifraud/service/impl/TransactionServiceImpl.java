package antifraud.service.impl;

import antifraud.configuration.TransactionProperty;
import antifraud.model.Card;
import antifraud.model.Transaction;
import antifraud.model.TransactionFeedback;
import antifraud.model.enums.TransactionResult;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.repository.TransactionRepository;
import antifraud.service.CardService;
import antifraud.service.StolenCardService;
import antifraud.service.SuspiciousIpService;
import antifraud.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static antifraud.exception.ExceptionMessages.TRANSACTION_NOT_FOUND;
import static antifraud.mappers.ModelMapper.transactionRequestToTransaction;
import static antifraud.model.enums.TransactionResult.ALLOWED;
import static antifraud.model.enums.TransactionResult.MANUAL_PROCESSING;
import static antifraud.model.enums.TransactionResult.PROHIBITED;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    final TransactionRepository transactionRepository;
    final StolenCardService stolenCardService;
    final SuspiciousIpService suspiciousIpService;
    final CardService cardService;
    final TransactionProperty transactionProperty;

    public TransactionServiceImpl(TransactionRepository transactionRepository, StolenCardService stolenCardService, SuspiciousIpService suspiciousIpService, CardService cardService, TransactionProperty transactionProperty) {
        this.transactionRepository = transactionRepository;
        this.stolenCardService = stolenCardService;
        this.suspiciousIpService = suspiciousIpService;
        this.cardService = cardService;
        this.transactionProperty = transactionProperty;
    }

    public TransactionResponse processTransaction(TransactionRequest transactionRequest) {
        Transaction transaction = transactionRequestToTransaction(transactionRequest);
        Card card = cardService.findByNumber(transactionRequest.getNumber());
        int maxAllowed = transactionProperty.getInitialMaxAllowed();
        int maxManual = transactionProperty.getInitialMaxManual();

        if (card != null) {
            maxAllowed = card.getMaxAllowed();
            maxManual = card.getMaxManual();
        } else {
            saveCard(transactionRequest.getNumber(), maxAllowed, maxManual);
        }
        transactionRepository.save(transaction);
        List<Transaction> transactionHistory = transactionRepository.findByNumberAndDateBetween
                (transactionRequest.getNumber(), transactionRequest.getDate().minusHours(1), transactionRequest.getDate());

        long uniqueIps = transactionHistory.stream().map(Transaction::getIp).distinct().count();
        long uniqueRegions = transactionHistory.stream().map(Transaction::getRegion).distinct().count();

        TransactionResult ipCorrelation = checkNumberOf(uniqueIps);
        TransactionResult regionCorrelation = checkNumberOf(uniqueRegions);
        TransactionResult amount = checkTransactionAmount(transactionRequest.getAmount(),maxAllowed,maxManual);
        TransactionResult stolenCard = checkForStolenCard(transactionRequest.getNumber()) ? PROHIBITED : ALLOWED;
        TransactionResult suspiciousIp = checkForSuspiciousIp(transactionRequest.getIp()) ? PROHIBITED : ALLOWED;

        List<TransactionResult> resultList = List.of(ipCorrelation, regionCorrelation, amount, stolenCard, suspiciousIp);
        TransactionResult transactionResult = getTransactionResult(resultList);

        transaction.setResult(transactionResult);
        transactionRepository.save(transaction);
        if (transactionResult == ALLOWED) return new TransactionResponse(ALLOWED, "none");
        if (transactionResult != PROHIBITED && amount == MANUAL_PROCESSING) {
            return new TransactionResponse(MANUAL_PROCESSING, "amount");
        }
        StringBuilder info = new StringBuilder()
                .append(amount == transactionResult ? "amount, " : "")
                .append(stolenCard == transactionResult ? "card-number, " : "")
                .append(suspiciousIp == transactionResult ? "ip, " : "")
                .append(ipCorrelation == transactionResult ? "ip-correlation, " : "")
                .append(regionCorrelation == transactionResult ? "region-correlation, " : "");
        info.setLength(info.length() - 2);
        return new TransactionResponse(transactionResult, info.toString());
    }

    private TransactionResult getTransactionResult(List<TransactionResult> list) {
        TransactionResult transactionResult = ALLOWED;
        for (var res : list) {
            if (res == MANUAL_PROCESSING) {
                transactionResult = MANUAL_PROCESSING;
            }
            if (res == PROHIBITED) {
                transactionResult = res;
                break;
            }
        }
        return transactionResult;
    }

    private void saveCard(String cardNumber, int maxAllowed, int maxManual) {
        Card card = new Card();
        card.setNumber(cardNumber);
        card.setMaxAllowed(maxAllowed);
        card.setMaxManual(maxManual);
        cardService.save(card);
    }

    private TransactionResult checkNumberOf(long nUniqueRequests) {
        if (nUniqueRequests < transactionProperty.getRegionAndIpLimit()) {
            return ALLOWED;
        }
        return nUniqueRequests == transactionProperty.getRegionAndIpLimit() ? MANUAL_PROCESSING :
                PROHIBITED;
    }

    private TransactionResult checkTransactionAmount(long amount,int maxAllowed,int maxManual) {
        if (amount <= maxAllowed) return ALLOWED;
        return amount <= maxManual ? MANUAL_PROCESSING : PROHIBITED;
    }


    private boolean checkForStolenCard(String number) {
        return stolenCardService.existsByNumber(number);
    }

    private boolean checkForSuspiciousIp(String ip) {
        return suspiciousIpService.existsByIp(ip);
    }

    public Transaction transactionFeedback(TransactionFeedback transactionFeedback) {
        Transaction transaction = transactionRepository.findById(transactionFeedback.getTransactionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Card card = cardService.findByNumber(transaction.getNumber());
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
                cardService.updateMaxAllowedAndMaxManualByNumber(maxAllowed, maxManual, transaction.getNumber());
            } else {
                cardService.updateMaxAllowedByNumber(maxAllowed, transaction.getNumber());
            }
            transaction.setFeedback(transactionFeedback.getFeedback());
        } else if (transactionFeedback.getFeedback() == MANUAL_PROCESSING) {
            if (transaction.getResult() == ALLOWED) {
                maxAllowed = decreaseMaxAllowed(transaction, maxAllowed);
                cardService.updateMaxAllowedByNumber(maxAllowed, transaction.getNumber());
            } else {
                maxManual = increaseMaxManual(transaction, maxManual);
                cardService.updateMaxManualByNumber(maxManual, transaction.getNumber());
            }
            transaction.setFeedback(transactionFeedback.getFeedback());
        } else if (transactionFeedback.getFeedback() == PROHIBITED) {
            maxManual = decreaseMaxManual(transaction, maxManual);
            if ((transaction.getResult() == MANUAL_PROCESSING)) {
                cardService.updateMaxManualByNumber(maxManual, transaction.getNumber());
            } else {
                maxAllowed = decreaseMaxAllowed(transaction, maxAllowed);
                cardService.updateMaxAllowedAndMaxManualByNumber(maxAllowed, maxManual, transaction.getNumber());
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, TRANSACTION_NOT_FOUND);
        } else {
            return list;
        }
    }
}
