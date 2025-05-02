package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.BlacklistToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BlacklistTokenRepository extends JpaRepository<BlacklistToken, UUID> {
    boolean existsByToken(String token);
}