package id.co.bcaf.solvr.model.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Feature {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;
//
//    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<RoleToFeature> roleToFeatures;

}
