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
            if (manual) {
                info.remove("amount");
            }
        }
        if (checkForSuspiciousIp(transactionRequest.getIp())) {
            transactionResponse.setResult(TransactionResult.PROHIBITED);
            info.add("ip");
            if (manual) {
                info.remove("amount");
            }
        }
        transactionRequestRepository.save(transactionRequest);

        String region = checkTransactionRegionsInLastHour(transactionRequest);
        if (region.equals("prohibited")) {
            transactionResponse.setResult(TransactionResult.PROHIBITED);
            info.add("region-correlation");
        }else if (region.equals("manual")){
            transactionResponse.setResult(TransactionResult.MANUAL_PROCESSING);
            info.add("region-correlation");
        }

        String ips = checkTransactionIpsInLastHour(transactionRequest);
        if (ips.equals("prohibited")) {
            transactionResponse.setResult(TransactionResult.PROHIBITED);
            info.add("ip-correlation");
        }else if (ips.equals("manual")){
            transactionResponse.setResult(TransactionResult.MANUAL_PROCESSING);
            info.add("ip-correlation");
        }

        if (info.isEmpty()) {
            transactionResponse.setInfo("none");
        } else {
            transactionResponse.setInfo(String.join(", ", info));
        }

        return transactionResponse;
    }

    private String checkTransactionIpsInLastHour(TransactionRequest transactionRequest) {
        List<String> ipCount = transactionRequestRepository.countOfRequestedIps
                (transactionRequest.getDate().minusHours(1), transactionRequest.getDate());
        System.out.println(ipCount);
        if (ipCount.size() < 3) {
            return "allow";
        } else if (ipCount.size() == 3) {
            return "manual";
        } else {
            return "prohibited";
        }
    }

    private String checkTransactionRegionsInLastHour(TransactionRequest transactionRequest) {
        List<Long> regionsCount = transactionRequestRepository.countOfRequestedRegions
                (transactionRequest.getDate().minusHours(1), transactionRequest.getDate());
        System.out.println(regionsCount);

        if (regionsCount.size() < 3) {
            return "allow";
        } else if (regionsCount.size() == 3) {
            return "manual";
        } else {
            return "prohibited";
        }

    }

    private boolean checkForStolenCard(String number) {
        return stolenCardRepository.existsByNumber(number);
    }

    private boolean checkForSuspiciousIp(String ip) {
        return suspiciousIpRepository.existsByIp(ip);
    }
}
