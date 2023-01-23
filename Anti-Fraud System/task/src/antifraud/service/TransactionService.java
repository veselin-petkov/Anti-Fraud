package antifraud.service;

import antifraud.model.*;
import org.springframework.stereotype.Service;


@Service
public class TransactionService {

    public TransactionResponse processTransaction(Long amount) {
        if (amount <= 200) {
            return new TransactionResponse(TransactionResult.ALLOWED);
        } else if (amount <= 1500) {
            return new TransactionResponse(TransactionResult.MANUAL_PROCESSING);
        } else {
            return new TransactionResponse(TransactionResult.PROHIBITED);
        }
    }
}
