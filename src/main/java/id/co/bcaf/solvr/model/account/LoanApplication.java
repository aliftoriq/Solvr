package id.co.bcaf.solvr.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_customer_id", nullable = false)
    private UserCustomer userCustomer;

//    @OneToMany(mappedBy = "loanApplication", cascade = CascadeType.ALL, orphanRemoval = true)
////    @JsonIgnore
//    private List<LoanApplicationToEmployee> loanApplicationToEmployees;

    @OneToMany(mappedBy = "loanApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanApplicationToEmployee> loanApplicationToEmployees = new ArrayList<>();

    private double loanAmount;
    private int loanTenor;
    private double monthlyPayment;
    private String status = "REQUESTED";
    private String housingStatus;

    @OneToOne
    @JsonIgnore
    private Loan loan;

    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime disbursedAt;

    @Column(nullable = true)
    private Double longitude;
    @Column(nullable = true)
    private Double latitude;
}
