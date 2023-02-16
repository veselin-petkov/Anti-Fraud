package java.antifraud.domain.service;

import antifraud.model.Card;
import antifraud.repository.CardRepository;
import antifraud.service.impl.CardServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.swing.assertions.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
@ExtendWith(MockitoExtension.class)
class RegularCardServiceTest {

    @InjectMocks
    private CardServiceImpl cardServiceImpl;
    @Mock
    private CardRepository cardRepository;

    private final String validCardNumber = "6250941006528599";

    @Test
    void whenRegularCardRepoIsEmptyThenFindByNumberShouldReturnNull() {
        Card regularCard = cardServiceImpl.findByNumber("");

        assertThat(regularCard).isNull();
    }

    @Test
    void whenCardRepoMatchesThenFindByNumberShouldCardAndShouldMatch() {
        Card customCard = new Card();
        customCard.setNumber(validCardNumber);
        given(cardRepository.findByNumber(validCardNumber)).willReturn(customCard);

        Card regularCard = cardRepository.findByNumber(validCardNumber);

        assertEquals(customCard.getNumber(), regularCard.getNumber());
    }

    @Test
    void whenRegularCardDoesNotMatchesThenFindByNumberShouldRegularCardAndShouldNotMatch() {
        Card customCard = new Card();
        customCard.setNumber(validCardNumber);
        String invalidCardNumber = "6250941006528588";
        Card invalidCard = new Card();
        customCard.setNumber(invalidCardNumber);

        given(cardRepository.findByNumber(invalidCardNumber)).willReturn(invalidCard);
        Card regularCard = cardRepository.findByNumber(invalidCardNumber);

        assertNotEquals(customCard.getNumber(), regularCard.getNumber());
    }

    @Test
    void whenRegularCardRepoIsEmptyThenExistsByNumberShouldReturnFalse() {
        boolean isPresent = cardServiceImpl.existsByNumber("");
        assertFalse(isPresent);
    }

    @Test
    void whenRegularCardExistsThenExistsByNumberShouldReturnTrue() {
        given(cardRepository.existsByNumber(validCardNumber)).willReturn(true);
        boolean isPresent = cardRepository.existsByNumber(validCardNumber);
        assertTrue(isPresent);
    }

    @Test
    void whenSavingRegularCardThenReturnTheRegularCard() {
        Card card = new Card();
        card.setNumber(validCardNumber);

        given(cardRepository.save(card)).willReturn(card);
        Card customRegularCard = cardServiceImpl.save(card);

        assertEquals(card, customRegularCard);
    }
}
