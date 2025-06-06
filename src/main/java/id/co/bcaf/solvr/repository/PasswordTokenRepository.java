package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.PasswordToken;
import id.co.bcaf.solvr.model.account.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordTokenRepository  extends JpaRepository<PasswordToken, UUID> {
    Optional<PasswordToken> findByToken(String token);
    boolean existsByToken(String token);

    Optional<PasswordToken> findByUser(User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordToken pt WHERE pt.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
