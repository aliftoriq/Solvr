package id.co.bcaf.solvr.model.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter

public class LoanApplication {
    @Id
    private UUID id;

    @ManyToOne
    private UserCustomer userCustomer;

    @ManyToOne
    private UserEmployee userEmployee;

    @ManyToOne
    private Feature feature;

    private double loanAmmount;
    private int loanTenor;
    private double montlyPayment;

    @OneToOne
    private Loan loan;
    private String status;


}
