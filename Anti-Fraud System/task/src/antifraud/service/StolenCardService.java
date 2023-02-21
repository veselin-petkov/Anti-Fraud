package antifraud.service;

import antifraud.model.StolenCard;
import antifraud.model.dto.StolenCardDTO;

import java.util.List;
import java.util.Optional;

public interface StolenCardService {
    Optional<StolenCard> addStolenCard(StolenCardDTO stolenCardDTO);
    List<StolenCard> listStolenCards();
    boolean deleteStolenCard(String number);

    boolean existsByNumber(String number);
}
