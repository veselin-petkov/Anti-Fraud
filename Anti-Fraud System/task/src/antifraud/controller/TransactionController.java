package antifraud.controller;

import antifraud.model.Transaction;
import antifraud.model.TransactionFeedback;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.service.TransactionService;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/antifraud")
@AllArgsConstructor
public class TransactionController {
    TransactionService transactionService;

    @PreAuthorize("hasRole('MERCHANT')")
    @PostMapping("/transaction")
    TransactionResponse transaction(@Valid @RequestBody TransactionRequest transactionRequest) {
        return transactionService.processTransaction(transactionRequest);
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @PutMapping("/transaction")
    Transaction transactionWithFeedback(@Valid @RequestBody TransactionFeedback transactionFeedback) {
        return transactionService.transactionFeedback(transactionFeedback);
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @GetMapping("/history")
    List<Transaction> transactionHistory() {
        return transactionService.listTransactions();
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @GetMapping("/history/{number}")
    List<Transaction> getTransactionByCardNumber(@CreditCardNumber @PathVariable("number") String number) {
        return transactionService.getTransactionById(number);
    }
}
