package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.LoanAplicationToEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanApplicationToEmployeeRepository extends JpaRepository<LoanAplicationToEmployee, UUID> {
}
