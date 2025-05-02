package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.LoanApplicationToEmployee;
import id.co.bcaf.solvr.model.account.UserEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface LoanApplicationToEmployeeRepository extends JpaRepository<LoanApplicationToEmployee, UUID> {
    Optional<LoanApplicationToEmployee> findByUserEmployee (UserEmployee userEmployee);
    Optional<LoanApplicationToEmployee> findByLoanApplicationIdAndUserEmployee_User_Id(UUID loanAppId, UUID userId);

    @Query("SELECT COUNT(lae) FROM LoanApplicationToEmployee lae WHERE lae.userEmployee.id = :userEmployeeId")
    Long countByUserEmployeeId(@Param("userEmployeeId") UUID userEmployeeId);
}
