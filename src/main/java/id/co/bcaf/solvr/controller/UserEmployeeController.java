package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.model.account.UserEmployee;
import id.co.bcaf.solvr.repository.UserCustomerRepository;
import id.co.bcaf.solvr.repository.UserEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user/employee")
public class UserEmployeeController {

    @Autowired
    private UserEmployeeRepository userEmployeeRepository;

    @GetMapping
    public ResponseEntity<?> getAllUserEmployee() {
        return ResponseEntity.ok(userEmployeeRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createUserEmployee(@RequestBody UserEmployee userEmployee) {
        return ResponseEntity.ok(userEmployeeRepository.save(userEmployee));
    }

}
