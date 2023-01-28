package antifraud.repository;

import antifraud.model.request.TransactionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRequestRepository extends JpaRepository<TransactionRequest, Long> {
    List<TransactionRequest> findByNumberAndDateBetween(String number, LocalDateTime dateStart, LocalDateTime dateEnd);
}