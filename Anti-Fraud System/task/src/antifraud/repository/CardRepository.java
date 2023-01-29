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
    @Query("update Card c set c.max_MANUAL = ?1 where c.number = ?2")
    int updateMax_MANUALByNumber(int max_MANUAL, String number);
    @Transactional
    @Modifying
    @Query("update Card c set c.max_ALLOWED = ?1 where c.number = ?2")
    int updateMax_ALLOWEDByNumber(int max_ALLOWED, String number);
 //   @Query("select max_ALLOWED from Card where number = ?1")
    int findMaxAllowedByNumber(String number);
 //   @Query("select max_MANUAL from Card where number = ?1")
    int findMaxManualByNumber(String number);
    Card findByNumber(String number);
}