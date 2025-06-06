package id.co.bcaf.solvr.model.account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
public class BlacklistToken {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    private String token;
    private LocalDateTime BlacklistDate;
}
