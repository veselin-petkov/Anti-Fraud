package antifraud.service;

import antifraud.model.DTO.SuspiciousIpDTO;
import antifraud.model.SuspiciousIp;
import antifraud.repository.SuspiciousIpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static antifraud.mappers.ModelMapper.suspiciousIpDTOtoSuspiciousIp;

@Service
public class SuspiciousIpService {
    @Autowired
    SuspiciousIpRepository suspiciousIpRepository;

    @Transactional
    public Optional<SuspiciousIp> addSuspiciousIp(SuspiciousIpDTO ipDTO) {
        if (suspiciousIpRepository.existsByIp(ipDTO.getIp())) {
            return Optional.empty();
        }
        SuspiciousIp ip = suspiciousIpDTOtoSuspiciousIp(ipDTO);
        return Optional.of(suspiciousIpRepository.save(ip));
    }

    @Transactional
    public boolean deleteSuspiciousIp(String ip) {
        return suspiciousIpRepository.deleteByIp(ip) == 1;
    }

    public List<SuspiciousIp> listSuspiciousIp() {
        return suspiciousIpRepository.findAll();
    }
}
