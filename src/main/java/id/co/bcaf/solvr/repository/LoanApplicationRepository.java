package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.LoanApplication;
import id.co.bcaf.solvr.model.account.UserCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {
    int countByUserCustomerAndStatusNot(UserCustomer user, String status);
    List<LoanApplication> findByUserCustomerAndStatusNot(UserCustomer user, String status);
    @Query("""
        SELECT l FROM LoanApplication l
        JOIN l.loanApplicationToEmployees lae
        WHERE lae.userEmployee.id = :employeeId
    """)
    List<LoanApplication> findByEmployeeId(UUID employeeId);
}
