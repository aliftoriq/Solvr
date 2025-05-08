package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.PlafonPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlafonPackageRepository extends JpaRepository<PlafonPackage, Long> {
    PlafonPackage findByLevel(Integer level);
}
