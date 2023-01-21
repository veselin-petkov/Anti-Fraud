package antifraud.service;

import antifraud.model.*;
import antifraud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TransactionService {
    @Autowired
    private final UserRepository userRepository;


    public TransactionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
