package antifraud.controller;

import antifraud.model.DTO.StolenCardDTO;
import antifraud.model.delete.DeleteCard;
import antifraud.model.StolenCard;
import antifraud.service.StolenCardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class StolenCardController {
    StolenCardService stolenCardService;

    @PreAuthorize("hasRole('SUPPORT')")
    @PostMapping("/api/antifraud/stolencard")
    StolenCard addStolenCard(@Valid @RequestBody StolenCardDTO stolenCardDTO){
        return stolenCardService.addStolenCard(stolenCardDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @GetMapping("/api/antifraud/stolencard")
    List<StolenCard> listStolenCards(){
        return stolenCardService.listStolenCards();
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @DeleteMapping("/api/antifraud/stolencard/{number}")
    DeleteCard deleteStolenCard(@PathVariable String number) {
        if (stolenCardService.deleteStolenCard(number)) {
            return new DeleteCard(number);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
