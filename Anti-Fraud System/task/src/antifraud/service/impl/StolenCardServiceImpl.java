package antifraud.service.impl;

import antifraud.model.StolenCard;
import antifraud.model.dto.StolenCardDTO;
import antifraud.repository.StolenCardRepository;
import antifraud.service.StolenCardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static antifraud.mappers.ModelMapper.stolenCardDTOtoStolenCard;

@Service
public class StolenCardServiceImpl implements StolenCardService {

    final StolenCardRepository stolenCardRepository;

    public StolenCardServiceImpl(StolenCardRepository stolenCardRepository) {
        this.stolenCardRepository = stolenCardRepository;
    }

    @Transactional
    public Optional<StolenCard> addStolenCard(StolenCardDTO stolenCardDTO) {
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
        return stolenCardRepository.deleteByNumber(number) == 1;
    }

    @Override
    public boolean existsByNumber(String number) {
        return stolenCardRepository.existsByNumber(number);
    }
}
