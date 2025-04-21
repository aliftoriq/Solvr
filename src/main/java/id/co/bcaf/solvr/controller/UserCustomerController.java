package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.model.account.UserCustomer;
import id.co.bcaf.solvr.repository.UserCustomerRepository;
import id.co.bcaf.solvr.services.UserCustomerService;
import id.co.bcaf.solvr.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/user/customer")
public class UserCustomerController {

    @Autowired
    private UserCustomerService userCustomerService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> getAllUserCustomer() {
        return ResponseEntity.ok(userCustomerService.getAllUserCustomers());
    }

    @PostMapping()
    public ResponseEntity<?> createUserCustomer(
            HttpServletRequest request,
            @RequestBody UserCustomer userCustomer
    ) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(userCustomerService.createUserCustomer(userCustomer, userId));
    }

}
