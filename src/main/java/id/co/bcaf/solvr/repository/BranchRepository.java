package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
}
