package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.Branch;
import id.co.bcaf.solvr.model.account.RoleToFeature;
import id.co.bcaf.solvr.model.account.User;
import id.co.bcaf.solvr.model.account.UserEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserEmployeeRepository extends JpaRepository<UserEmployee, UUID> {
    List<UserEmployee> findByBranchId(UUID branchId);

    // LoanApplicationToEmployeeRepository.java
    @Query("SELECT COUNT(lae) FROM LoanApplicationToEmployee lae WHERE lae.userEmployee.id = :userEmployeeId")
    Long countByUserEmployeeId(@Param("userEmployeeId") UUID userEmployeeId);
}
