package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.dto.ResponseTemplate;
import id.co.bcaf.solvr.dto.user.UserCustomerRequest;
import id.co.bcaf.solvr.dto.user.UserCustomerResponse;
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
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", userCustomerService.getAllUserCustomers()));
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getUserCustomerDetail(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", userCustomerService.getUserCustomerDetail(userId)));
    }

    @PostMapping()
    public ResponseEntity<?> createUserCustomer(HttpServletRequest request, @RequestBody UserCustomerRequest userCustomer) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", userCustomerService.createUserCustomer(userCustomer, userId)));
    }

    @PutMapping()
    public ResponseEntity<?> updateUserCustomer(HttpServletRequest request, @RequestBody UserCustomerRequest userCustomer) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", userCustomerService.updateUserCustomer(userId, userCustomer)));
    }

}
