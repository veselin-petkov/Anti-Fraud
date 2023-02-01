package antifraud.service;

import antifraud.model.StolenCard;
import antifraud.model.dto.StolenCardDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface StolenCardService {
    Optional<StolenCard> addStolenCard(StolenCardDTO stolenCardDTO);
    List<StolenCard> listStolenCards();
    boolean deleteStolenCard(String number);
}
