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

    @Override
    public void updateMaxAllowedAndMaxManualByNumber(int maxAllowed, int maxManual, String number) {
        cardRepository.updateMaxAllowedAndMaxManualByNumber(maxAllowed, maxManual, number);
    }

    @Override
    public void updateMaxAllowedByNumber(int maxAllowed, String number) {
        cardRepository.updateMaxAllowedByNumber(maxAllowed, number);
    }

    @Override
    public void updateMaxManualByNumber(int maxManual, String number) {
        cardRepository.updateMaxManualByNumber(maxManual, number);
    }
}