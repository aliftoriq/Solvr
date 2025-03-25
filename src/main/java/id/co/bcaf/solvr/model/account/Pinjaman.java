package id.co.bcaf.solvr.model.account;

import jakarta.persistence.*;

import java.util.UUID;


@Entity
@Table(name = "pinjaman")
public class Pinjaman {
    @Id
    private UUID id = UUID.randomUUID();

    private UUID id_pinjaman;
    private UUID id_user;
    private UUID id_pengajuan;
    private double jumlah_pinjaman;
    private int tenor;
    private double angsuran;
    private int sisa_tenor;
    private double sisa_pokok_hutang;

}
