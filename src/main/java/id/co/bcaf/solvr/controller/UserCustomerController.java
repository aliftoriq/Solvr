package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.model.account.UserCustomer;
import id.co.bcaf.solvr.repository.UserCustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user/customer")
public class UserCustomerController {

    @Autowired
    private UserCustomerRepository userCustomerRepository;

    @GetMapping
    public ResponseEntity<?> getAllUserCustomer() {
        return ResponseEntity.ok(userCustomerRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> creaateUserCustomer(@RequestBody UserCustomer userCustomer) {
        return ResponseEntity.ok(userCustomerRepository.save(userCustomer));
    }

}
