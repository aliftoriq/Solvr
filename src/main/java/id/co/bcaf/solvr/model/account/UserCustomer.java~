package id.co.bcaf.solvr.model.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@Entity
public class UserCustomer {
    @Id
    private int id;
    private int customerId;
    private String nik;
    private String address;
    private String phone;
    private String motherName;
    private Date birthDate;
    private String housingStatus;
    private Double monthlyIncome;

    @ManyToOne
    Branch branch;

    @OneToMany
    private Set<LoanApplication> loanApplications;
}
