package id.co.bcaf.solvr.model.account;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class UserEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String nip;
    private String email;
    private String department;

    @ManyToOne
    Branch branch;

    @OneToMany
    private Set<LoanApplication> loanApplications;
}