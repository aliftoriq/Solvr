package id.co.bcaf.solvr.repository;

import id.co.bcaf.solvr.model.account.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

}