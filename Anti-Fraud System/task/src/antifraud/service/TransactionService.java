package antifraud.service;

import antifraud.model.enums.TransactionResult;
import antifraud.model.request.TransactionRequest;
import antifraud.repository.TransactionRequestRepository;
import antifraud.model.response.TransactionResponse;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static antifraud.model.enums.TransactionResult.*;

@Slf4j
@Service
public class TransactionService {
    @Autowired
    private TransactionRequestRepository transactionRequestRepository;
    @Autowired
    StolenCardRepository stolenCardRepository;
    @Autowired
    SuspiciousIpRepository suspiciousIpRepository;

    public TransactionResponse processTransaction(TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse = new TransactionResponse();
        List<String> info = new ArrayList<>();

        boolean manual = false;
        if (transactionRequest.getAmount() <= 200) {
            transactionResponse.setResult(ALLOWED);
        } else if (transactionRequest.getAmount() <= 1500) {
            transactionResponse.setResult(MANUAL_PROCESSING);
            info.add("amount");
            manual = true;
        } else {
            transactionResponse.setResult(PROHIBITED);
            info.add("amount");
        }
        if (checkForStolenCard(transactionRequest.getNumber())) {
            transactionResponse.setResult(PROHIBITED);
            info.add("card-number");
            if (manual) {
                info.remove("amount");
            }
        }
        if (checkForSuspiciousIp(transactionRequest.getIp())) {
            transactionResponse.setResult(PROHIBITED);
            info.add("ip");
            if (manual) {
                info.remove("amount");
            }
        }
        transactionRequestRepository.save(transactionRequest);
        List<TransactionRequest> transactionHistory = transactionRequestRepository.findByNumberAndDateBetween
                (transactionRequest.getNumber(),transactionRequest.getDate().minusHours(1), transactionRequest.getDate());

        long uniqueIps = transactionHistory.stream().map(TransactionRequest::getIp).distinct().count();
        long uniqueRegions = transactionHistory.stream().map(TransactionRequest::getRegion).distinct().count();

        if (checkNumberOf(uniqueRegions).equals(PROHIBITED)) {
            transactionResponse.setResult(PROHIBITED);
            info.add("region-correlation");
        }else if (checkNumberOf(uniqueRegions).equals(MANUAL_PROCESSING)){
            transactionResponse.setResult(MANUAL_PROCESSING);
            info.add("region-correlation");
        }

        if (checkNumberOf(uniqueIps).equals(PROHIBITED)) {
            transactionResponse.setResult(PROHIBITED);
            info.add("ip-correlation");
        }else if (checkNumberOf(uniqueIps).equals(MANUAL_PROCESSING)){
            transactionResponse.setResult(MANUAL_PROCESSING);
            info.add("ip-correlation");
        }

        if (info.isEmpty()) {
            transactionResponse.setInfo("none");
        } else {
            transactionResponse.setInfo(String.join(", ", info));
        }

        return transactionResponse;
    }

    private TransactionResult checkNumberOf(long nUniqueRequests) {
        if (nUniqueRequests <= 2) {
            return ALLOWED;
        }
        return nUniqueRequests == 3 ? MANUAL_PROCESSING :
                        PROHIBITED;
    }

    private boolean checkForStolenCard(String number) {
        return stolenCardRepository.existsByNumber(number);
    }

    private boolean checkForSuspiciousIp(String ip) {
        return suspiciousIpRepository.existsByIp(ip);
    }
}
