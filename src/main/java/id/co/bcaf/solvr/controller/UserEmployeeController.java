package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.dto.ResponseTemplate;
import id.co.bcaf.solvr.dto.user.UserEmployeeRequest;
import id.co.bcaf.solvr.model.account.UserEmployee;
import id.co.bcaf.solvr.repository.UserCustomerRepository;
import id.co.bcaf.solvr.repository.UserEmployeeRepository;
import id.co.bcaf.solvr.services.UserEmployeeService;
import id.co.bcaf.solvr.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/user/employee")
public class UserEmployeeController {

    @Autowired
    private UserEmployeeService userEmployeeService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> getAllUserEmployee() {
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", userEmployeeService.getAllUserEmployee()));
    }

    @PostMapping
    public ResponseEntity<?> createUserEmployee(HttpServletRequest request, @RequestBody UserEmployeeRequest userEmployee) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", userEmployeeService.createUserEmployee(userEmployee)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserEmployee(@PathVariable UUID id, @RequestBody UserEmployeeRequest userEmployee) {
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", userEmployeeService.updateUserEmployee(id, userEmployee)));
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getUserEmployeeByUserId(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", userEmployeeService.getUserEmployeeByUserId(userId)));
    }

}
