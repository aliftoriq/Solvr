package id.co.bcaf.solvr.model.account;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
public class LoanApplicationToEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_employee_id", nullable = false)
    private UserEmployee userEmployee;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;

    @Column(columnDefinition = "TEXT")
    private String notes;

}
