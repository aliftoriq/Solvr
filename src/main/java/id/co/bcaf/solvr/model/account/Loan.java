package id.co.bcaf.solvr.model.account;

import jakarta.persistence.*;

import java.util.UUID;


@Entity
@Table(name = "pinjaman")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private UserCustomer userCustomer;

    private Double ammount;
    private int tenor;
    private int sisa_tenor;
    private Double angsuran;
    private Double sisa_pokok_hutang;
    private String status;



}
