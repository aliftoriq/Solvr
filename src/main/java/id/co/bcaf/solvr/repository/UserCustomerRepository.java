package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.RoleToFeature;
import id.co.bcaf.solvr.model.account.UserCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserCustomerRepository extends JpaRepository<UserCustomer, UUID> {
}
