package id.co.bcaf.solvr.model.account;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Setter
@Getter
@Entity
@SQLDelete(sql="update users set deleted = true where id = ?")
@SQLRestriction("deleted=false")
@Table(name = "users")
public class Users {
    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role", referencedColumnName = "id")
    private Role role;

    private boolean deleted = Boolean.FALSE;
}