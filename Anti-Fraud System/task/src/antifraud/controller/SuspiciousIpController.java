package antifraud.controller;

import antifraud.model.DTO.SuspiciousIpDTO;
import antifraud.model.SuspiciousIp;
import antifraud.model.delete.DeletedIp;
import antifraud.service.SuspiciousIpService;
import antifraud.validation.IpAddress;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@PreAuthorize("hasRole('SUPPORT')")
@RestController
@RequestMapping("/api/antifraud/suspicious-ip")
@AllArgsConstructor
@Validated
public class SuspiciousIpController {
    SuspiciousIpService suspiciousIpService;

    @PostMapping
    SuspiciousIp addSuspiciousIp(@Valid @RequestBody SuspiciousIpDTO ip) {
        return suspiciousIpService.addSuspiciousIp(ip)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }
    @GetMapping
    List<SuspiciousIp> listSuspiciousIp(){
        return suspiciousIpService.listSuspiciousIp();
    }

    @DeleteMapping("/{ip}")
    DeletedIp deleteSuspiciousIp(@IpAddress @PathVariable String ip) {
        if (suspiciousIpService.deleteSuspiciousIp(ip)) {
            return new DeletedIp(ip);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
