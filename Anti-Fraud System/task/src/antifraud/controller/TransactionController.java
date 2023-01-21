package antifraud.controller;

import antifraud.model.*;
import antifraud.model.DTO.UserDTO;
import antifraud.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class TransactionController {
    TransactionService transactionService;

    @PostMapping("/api/antifraud/transaction")
    TransactionResponse transaction(@RequestBody @Valid TransactionRequest transactionRequest){
        return transactionService.processTransaction(transactionRequest.getAmount());
    }
}
