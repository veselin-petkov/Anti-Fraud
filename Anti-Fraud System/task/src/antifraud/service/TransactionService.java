package antifraud.service;

import antifraud.model.*;
import antifraud.model.enums.TransactionResult;
import antifraud.model.request.TransactionRequest;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TransactionService {
    @Autowired
    StolenCardRepository stolenCardRepository;
    @Autowired
    SuspiciousIpRepository suspiciousIpRepository;

    public TransactionResponse processTransaction(TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse = new TransactionResponse();
        List<String> info = new ArrayList<>();
        boolean manual = false;
        if (transactionRequest.getAmount() <= 200) {
            transactionResponse.setResult(TransactionResult.ALLOWED);
        } else if (transactionRequest.getAmount() <= 1500) {
            transactionResponse.setResult(TransactionResult.MANUAL_PROCESSING);
            info.add("amount");
            manual = true;
        } else {
            transactionResponse.setResult(TransactionResult.PROHIBITED);
            info.add("amount");
        }
        if (checkForStolenCard(transactionRequest.getNumber())) {
            transactionResponse.setResult(TransactionResult.PROHIBITED);
            info.add("card-number");
            if (manual){
                info.remove("amount");
            }
        }
        if (checkForSuspiciousIp(transactionRequest.getIp())) {
            transactionResponse.setResult(TransactionResult.PROHIBITED);
            info.add("ip");
            if (manual){
                info.remove("amount");
            }
        }

        if (info.isEmpty()) {
            transactionResponse.setInfo("none");
        } else {
            transactionResponse.setInfo(String.join(", ", info));
        }
        return transactionResponse;
    }

    private boolean checkForStolenCard(String number) {
        return stolenCardRepository.existsByNumber(number);
    }

    private boolean checkForSuspiciousIp(String ip) {
        return suspiciousIpRepository.existsByIp(ip);
    }
}
