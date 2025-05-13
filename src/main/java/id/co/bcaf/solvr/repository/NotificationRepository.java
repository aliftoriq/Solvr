package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.NotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationHistory, UUID> {
}