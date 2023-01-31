package antifraud.controller;

import antifraud.model.StolenCard;
import antifraud.model.delete.DeleteCard;
import antifraud.model.dto.StolenCardDTO;
import antifraud.service.StolenCardService;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@PreAuthorize("hasRole('SUPPORT')")
@RestController
@RequestMapping("/api/antifraud/stolencard")
@AllArgsConstructor
@Validated
public class StolenCardController {
    StolenCardService stolenCardService;

    @PostMapping
    StolenCard addStolenCard(@Valid @RequestBody StolenCardDTO stolenCardDTO) {
        return stolenCardService.addStolenCard(stolenCardDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }

    @GetMapping
    List<StolenCard> listStolenCards() {
        return stolenCardService.listStolenCards();
    }

    @DeleteMapping("/{number}")
    DeleteCard deleteStolenCard(@CreditCardNumber @PathVariable String number) {
        if (stolenCardService.deleteStolenCard(number)) {
            return new DeleteCard(number);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
