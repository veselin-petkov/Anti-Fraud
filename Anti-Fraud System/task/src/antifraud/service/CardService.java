package antifraud.service;

import antifraud.model.Card;

public interface CardService {

    boolean existsByNumber(String cardNumber);

    Card findByNumber(String cardNumber);

    Card save(Card regularCard);
}
