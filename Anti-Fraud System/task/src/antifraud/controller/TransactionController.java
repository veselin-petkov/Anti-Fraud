package antifraud.controller;

import antifraud.model.TransactionFeedback;
import antifraud.model.Transaction;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.service.TransactionService;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@PreAuthorize("hasRole('MERCHANT')")
@RestController("/api/antifraud")
@AllArgsConstructor
public class TransactionController {
    TransactionService transactionService;

    @PostMapping("/transaction")
    TransactionResponse transaction(@Valid @RequestBody TransactionRequest transactionRequest){
        return transactionService.processTransaction(transactionRequest);
    }

    @PutMapping("/transaction")
    Transaction transactionWithFeedback(@Valid @RequestBody TransactionFeedback transactionFeedback){
        return transactionService.transactionFeedback(transactionFeedback);
    }

    @GetMapping("/history")
    List<Transaction> transactionHistory(){
        return transactionService.listTransactions();
    }

    @GetMapping("/history/{number}")
    List<Transaction> getTransactionById(@CreditCardNumber @PathVariable String number){
        return transactionService.getTransactionById(number);
    }
}
