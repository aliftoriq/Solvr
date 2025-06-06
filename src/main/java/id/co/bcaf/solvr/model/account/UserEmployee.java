package id.co.bcaf.solvr.model.account;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonBackReference("branch-userEmployee")
    private Branch branch;

    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<LoanApplication> loanApplications;

    @OneToOne(mappedBy = "userEmployee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private User user;
}