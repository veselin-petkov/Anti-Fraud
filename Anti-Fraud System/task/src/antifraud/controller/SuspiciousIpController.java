package antifraud.controller;

import antifraud.model.DTO.SuspiciousIpDTO;
import antifraud.model.delete.DeletedIp;
import antifraud.model.SuspiciousIp;
import antifraud.service.SuspiciousIpService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class SuspiciousIpController {
    SuspiciousIpService suspiciousIpService;

    @PreAuthorize("hasRole('SUPPORT')")
    @PostMapping("/api/antifraud/suspicious-ip")
    SuspiciousIp addSuspiciousIp(@Valid @RequestBody SuspiciousIpDTO ip) {
        return suspiciousIpService.addSuspiciousIp(ip)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }
    @PreAuthorize("hasRole('SUPPORT')")
    @GetMapping("/api/antifraud/suspicious-ip")
    List<SuspiciousIp> listSuspiciousIp(){
        return suspiciousIpService.listSuspiciousIp();
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    DeletedIp deleteSuspiciousIp(@PathVariable String ip) {
        if (suspiciousIpService.deleteSuspiciousIp(ip)) {
            return new DeletedIp(ip);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
