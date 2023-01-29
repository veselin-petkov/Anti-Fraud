package antifraud.repository;

import antifraud.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByNumberAndDateBetween(String number, LocalDateTime dateStart, LocalDateTime dateEnd);

    Optional<Transaction> findByNumber(String number);

    List<Transaction> findAllByNumber(String number);
}