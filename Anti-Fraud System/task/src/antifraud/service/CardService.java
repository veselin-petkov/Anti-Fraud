package antifraud.service;

import antifraud.model.Card;

public interface CardService {

    boolean existsByNumber(String cardNumber);

    Card findByNumber(String cardNumber);

    Card save(Card regularCard);

    void updateMaxAllowedAndMaxManualByNumber(int maxAllowed, int maxManual, String number);

    void updateMaxAllowedByNumber(int maxAllowed, String number);

    void updateMaxManualByNumber(int maxManual, String number);
}
