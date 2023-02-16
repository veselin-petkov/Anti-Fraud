package antifraud.service.impl;

import antifraud.model.Card;
import antifraud.repository.CardRepository;
import antifraud.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;

    @Override
    public boolean existsByNumber(String cardNumber) {
        return cardRepository.existsByNumber(cardNumber);
    }

    @Override
    public Card findByNumber(String cardNumber) {
        return cardRepository.findByNumber(cardNumber);
    }

    @Override
    public Card save(Card regularCard) {
        return cardRepository.save(regularCard);
    }
}