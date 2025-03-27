package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.Feature;
import id.co.bcaf.solvr.model.account.Role;
import id.co.bcaf.solvr.model.account.RoleToFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleToFeatureRepository extends JpaRepository<RoleToFeature, UUID> {
    Optional<RoleToFeature> findByRole(Role role);
}
