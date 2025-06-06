package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByRoleId(int id);

    List<User> findByRoleNameIn(List<String> roles);

    Long countByRoleIn(List<String> roles);
}