package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {

}
