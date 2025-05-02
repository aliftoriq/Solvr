package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.PlafonPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlafonPackageRepository extends JpaRepository<PlafonPackage, Long> {
}
