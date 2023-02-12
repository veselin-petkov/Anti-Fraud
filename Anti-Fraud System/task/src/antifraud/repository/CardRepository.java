package antifraud.repository;

import antifraud.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    @Transactional
    @Modifying
    @Query("update Card c set c.maxAllowed = ?1, c.maxManual = ?2 where c.number = ?3")
    int updateMaxAllowedAndMaxManualByNumber(int max_ALLOWED, int max_MANUAL, String number);

    @Transactional
    @Modifying
    @Query("update Card c set c.maxManual = ?1 where c.number = ?2")
    int updateMaxManualByNumber(int max_MANUAL, String number);

    @Transactional
    @Modifying
    @Query("update Card c set c.maxAllowed = ?1 where c.number = ?2")
    int updateMaxAllowedByNumber(int max_ALLOWED, String number);

    Card findByNumber(String number);
}