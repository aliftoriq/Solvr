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

    List<LoanApplication> findByUserCustomerAndStatus(UserCustomer user, String status);

    @Query("""
                SELECT l FROM LoanApplication l
                WHERE l.userCustomer = :userCustomer AND l.status NOT IN :statuses
            """)
    List<LoanApplication> findByUserCustomerAndStatusNotIn(
            @Param("userCustomer") UserCustomer userCustomer,
            @Param("statuses") List<String> statuses
    );

    @Query("""
                SELECT l FROM LoanApplication l
                JOIN l.loanApplicationToEmployees lae
                WHERE lae.userEmployee.id = :employeeId
            """)
    List<LoanApplication> findByEmployeeId(UUID employeeId);

    List<LoanApplication> findRequestedByUserCustomerId(UUID userCustomerId);

    @Query(value = "SELECT COUNT(*) FROM loan_application WHERE status = :status", nativeQuery = true)
    long countByStatus(@Param("status") String status);


    @Query(value = "SELECT COALESCE(SUM(loan_amount), 0) FROM loan_application WHERE status = :status", nativeQuery = true)
    double sumLoanAmountByStatus(@Param("status") String status);

    @Query(value = "SELECT COALESCE(SUM(loan_amount), 0) FROM loan_application WHERE status = 'DISBURSEMENT'", nativeQuery = true)
    double sumOutstandingLoan();


    @Query(value = """
                SELECT SUM(la.loan_amount * pp.interest_rate * la.loan_tenor / 12)
                FROM loan_application la
                JOIN user_customer uc ON la.user_customer_id = uc.id
                JOIN plafon_package pp ON uc.plafon_package_id = pp.id
                WHERE la.status = 'DISBURSEMENT'
            """, nativeQuery = true)
    Double sumInterestGenerated();


}
