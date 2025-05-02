package id.co.bcaf.solvr.model.account;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Entity
public class UserCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nik;
    private String address;
    private String phone;
    private String motherName;
    private Date birthDate;
    private String housingStatus;
    private Double monthlyIncome;

    @OneToMany
    private Set<LoanApplication> loanApplications;

    @ManyToOne
    private PlafonPackage plafonPackage;

    private Long totalPinjamanLunas;

    @OneToOne(mappedBy = "userCustomer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private User user;
}
