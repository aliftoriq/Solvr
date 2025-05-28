package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.FirebaseToken;
import id.co.bcaf.solvr.model.account.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface FirebaseTokenRepository extends JpaRepository<FirebaseToken, UUID> {
    List<FirebaseToken> findByUser_Id(UUID userId);
    FirebaseToken findByToken(String token);
}