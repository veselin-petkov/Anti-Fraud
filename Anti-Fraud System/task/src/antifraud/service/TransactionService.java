package antifraud.service;

import antifraud.mappers.ModelMapper;
import antifraud.model.*;
import antifraud.model.DTO.UserDTO;
import antifraud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static antifraud.mappers.ModelMapper.userDTOtoUser;
import static antifraud.mappers.ModelMapper.userToUserResponse;

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
