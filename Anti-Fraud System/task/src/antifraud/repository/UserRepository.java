package antifraud.repository;

import antifraud.model.User;
import antifraud.model.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    @Modifying
    @Query("update User u set u.role = ?1 where u.id = ?2")
    int updateRoleById(Roles role, Long id);

    @Modifying
    @Query("update User u set u.role = ?1 where u.username = ?2")
    int updateRoleByUsername(Roles role, String username);
    Optional<User> findByUsername(String username);

    long deleteUserByUsername( String username);
}