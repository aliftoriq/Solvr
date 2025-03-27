package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.RoleToFeature;
import id.co.bcaf.solvr.model.account.UserEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserEmployeeRepository extends JpaRepository<UserEmployee, UUID> {
}
