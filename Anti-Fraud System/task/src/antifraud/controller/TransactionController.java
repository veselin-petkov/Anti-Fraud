package antifraud.controller;

import antifraud.model.*;
import antifraud.model.request.TransactionRequest;
import antifraud.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class TransactionController {
    TransactionService transactionService;

    @PostMapping("/api/antifraud/transaction")
    TransactionResponse transaction(@RequestBody @Valid TransactionRequest transactionRequest){
        return transactionService.processTransaction(transactionRequest);
    }
}
