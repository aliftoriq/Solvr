package id.co.bcaf.solvr.model.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter

public class Plafon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private double plafon;

    @OneToOne
    UserCustomer userCustomer;

}
