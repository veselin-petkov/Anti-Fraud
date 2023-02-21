package antifraud.service;

import antifraud.model.SuspiciousIp;
import antifraud.model.dto.SuspiciousIpDTO;

import java.util.List;
import java.util.Optional;

public interface SuspiciousIpService {

    Optional<SuspiciousIp> addSuspiciousIp(SuspiciousIpDTO ipDTO);

    boolean deleteSuspiciousIp(String ip);

    List<SuspiciousIp> listSuspiciousIp();

    boolean existsByIp(String ip);
}