package antifraud.service;

import antifraud.model.DTO.StolenCardDTO;
import antifraud.model.StolenCard;
import antifraud.repository.StolenCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static antifraud.mappers.ModelMapper.stolenCardDTOtoStolenCard;
import static antifraud.utils.Utils.checkCardNumber;

@Service
public class StolenCardService {

    @Autowired
    StolenCardRepository stolenCardRepository;

    @Transactional
    public Optional<StolenCard> addStolenCard(StolenCardDTO stolenCardDTO) {
        checkCardNumber(stolenCardDTO.getNumber());
        if (stolenCardRepository.existsByNumber(stolenCardDTO.getNumber())) {
            return Optional.empty();
        }
        StolenCard stolenCard = stolenCardDTOtoStolenCard(stolenCardDTO);
        return Optional.of(stolenCardRepository.save(stolenCard));
    }

    public List<StolenCard> listStolenCards() {
        return stolenCardRepository.findAll();
    }

    @Transactional
    public boolean deleteStolenCard(String number) {
        checkCardNumber(number);
        return stolenCardRepository.deleteByNumber(number) == 1;
    }
}
