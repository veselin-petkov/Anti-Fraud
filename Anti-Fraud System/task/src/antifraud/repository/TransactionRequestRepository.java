package antifraud.repository;

import antifraud.model.request.TransactionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRequestRepository extends JpaRepository<TransactionRequest, Long> {

    @Query(nativeQuery = true,value = "SELECT region from TRANSACTION_REQUESTS " +
            "WHERE DATE BETWEEN ?1 AND ?2 " +
            "group by region")
    List<Long> countOfRequestedRegions(LocalDateTime dateStart, LocalDateTime dateEnd);


    @Query(nativeQuery = true,value = "SELECT ip from TRANSACTION_REQUESTS " +
            "WHERE DATE BETWEEN ?1 AND ?2 " +
            "group by ip")
    List<String> countOfRequestedIps(LocalDateTime dateStart, LocalDateTime dateEnd);
}