package id.co.bcaf.solvr.model.account;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Setter
@Getter
@SQLDelete(sql="update users set deleted = true where id = ?")
@SQLRestriction("deleted=false")
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String status;

    @ManyToOne
    @JoinColumn(name = "role", referencedColumnName = "id")
    private Role role;

    @OneToOne
    private UserCustomer userCustomer;

    @OneToOne
    private UserEmployee userEmployee;

    private boolean deleted = Boolean.FALSE;
}