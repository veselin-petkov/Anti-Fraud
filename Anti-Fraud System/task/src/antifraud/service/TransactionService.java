package antifraud.service;

import antifraud.model.Transaction;
import antifraud.model.TransactionFeedback;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse processTransaction(TransactionRequest transactionRequest);

    Transaction transactionFeedback(TransactionFeedback transactionFeedback);

    List<Transaction> listTransactions();

    List<Transaction> getTransactionById(String number);
}
