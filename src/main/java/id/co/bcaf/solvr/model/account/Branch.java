package id.co.bcaf.solvr.model.account;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;

    @Column(nullable = true)
    private double longitude;

    @Column(nullable = true)
    private double latitude;

    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY)
    @JsonManagedReference("branch-userEmployee")
    private Set<UserEmployee> employees;
}
