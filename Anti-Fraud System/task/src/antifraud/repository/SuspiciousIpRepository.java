package antifraud.repository;

import antifraud.model.SuspiciousIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuspiciousIpRepository extends JpaRepository<SuspiciousIp, Long> {
    long deleteByIp(String ip);
    boolean existsByIp(String ip);

}
