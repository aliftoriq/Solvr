package id.co.bcaf.solvr.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter

public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
//    @JoinColumn(name = "user_customer_id", nullable = false)
    private UserCustomer userCustomer;

    @OneToMany(mappedBy = "loanApplication", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
    private List<LoanAplicationToEmployee> loanApplicationToEmployees;

    private double loanAmmount;
    private int loanTenor;
    private double montlyPayment;
    private String status;
    private String housing_status;

    @OneToOne
    private Loan loan;


}
