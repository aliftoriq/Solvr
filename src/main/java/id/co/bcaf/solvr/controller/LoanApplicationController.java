package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.dto.ResponseTemplate;
import id.co.bcaf.solvr.dto.loan.LoanApplicationRequest;
import id.co.bcaf.solvr.dto.loan.SimulationRequest;
import id.co.bcaf.solvr.dto.loan.StatusRequest;
import id.co.bcaf.solvr.model.account.LoanApplication;
import id.co.bcaf.solvr.services.LoanApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loan-application")
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService){
        this.loanApplicationService = loanApplicationService;
    }

//    @Secured("ROLE_CUSTOMER")
    @PostMapping
    public ResponseEntity<?> createLoanApplication(HttpServletRequest request, @RequestBody LoanApplicationRequest loanApplication) {
        UUID userId = (UUID) request.getAttribute("userId");
        try {
            LoanApplication created = loanApplicationService.createApplication(userId, loanApplication);
            ResponseTemplate<LoanApplication> response = new ResponseTemplate<>(200, "Pengajuan berhasil dibuat", created);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            ResponseTemplate<LoanApplication> errorResponse = new ResponseTemplate<>(400, e.getMessage(), null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (RuntimeException e) {
            ResponseTemplate<LoanApplication> errorResponse = new ResponseTemplate<>(500, "Terjadi kesalahan: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllLoanApplication() {
        return ResponseEntity.ok(loanApplicationService.getAllApplications());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getLoanApplicationById(@PathVariable UUID id) {
        return ResponseEntity.ok(loanApplicationService.getApplicationById(id));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getAllLoanApplicationHistory(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(loanApplicationService.getAllLoanApplicationHistory(userId));
    }

    @GetMapping("customer/history")
    public ResponseEntity<?> getAllLoanApplicationHistoryByUserId(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", loanApplicationService.getAllCustomerHistory(userId)));
    }

    @GetMapping("/marketing")
    public ResponseEntity<?> getAllLoanApplicationByUserId(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(loanApplicationService.getAllCustomerMarketing(userId));
    }

    @GetMapping("/branch-manager")
    public ResponseEntity<?> getAllLoanApplicationBranchManager(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(loanApplicationService.getAllCustomerBranchManager(userId));
    }

    @GetMapping("/backoffice")
    public ResponseEntity<?> getAllLoanApplicationBackOffice(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(loanApplicationService.getAllCustomerBackOffice(userId));
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<ResponseTemplate<LoanApplication>> reviewLoanApplication(HttpServletRequest request, @PathVariable UUID id, @RequestBody StatusRequest notes) {
        UUID userId = (UUID) request.getAttribute("userId");
        try {
            LoanApplication reviewed = loanApplicationService.reviewLoanApplication(userId, id, notes.getNotes());
            return ResponseEntity.ok(new ResponseTemplate<>(200, "Loan application reviewed by marketing.", reviewed));
        }catch (Exception e) {
            ResponseTemplate<LoanApplication> errorResponse = new ResponseTemplate<>(500, "Terjadi kesalahan: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveLoanApplication(HttpServletRequest request, @PathVariable UUID id, @RequestBody StatusRequest notes) {
        UUID userId = (UUID) request.getAttribute("userId");
        try {
            LoanApplication reviewed = loanApplicationService.approveLoanApplication(userId,id, notes.getNotes());
            return ResponseEntity.ok(new ResponseTemplate<>(200, "Loan application approved by Bracnh manager.", reviewed));
        }catch (Exception e) {
            ResponseTemplate<LoanApplication> errorResponse = new ResponseTemplate<>(500, "Terjadi kesalahan: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }

    @PutMapping("/{id}/disburse")
    public ResponseEntity<?> disburseLoanApplication(HttpServletRequest request, @PathVariable UUID id, @RequestBody StatusRequest notes) {
        UUID userId = (UUID) request.getAttribute("userId");
        try {
            LoanApplication reviewed = loanApplicationService.disburseLoanApplication(userId, id, notes.getNotes());
            return ResponseEntity.ok(new ResponseTemplate<>(200, "Loan application Disburse.", reviewed));
        }catch (Exception e) {
            ResponseTemplate<LoanApplication> errorResponse = new ResponseTemplate<>(500, "Terjadi kesalahan: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectLoanApplication(HttpServletRequest request, @PathVariable UUID id, @RequestBody StatusRequest notes) {
        UUID userId = (UUID) request.getAttribute("userId");
        try {
            LoanApplication reviewed = loanApplicationService.rejectLoanApplication(userId, id, notes.getNotes());
            return ResponseEntity.ok(new ResponseTemplate<>(200, "Loan application rejected.", reviewed));
        }catch (Exception e) {
            ResponseTemplate<LoanApplication> errorResponse = new ResponseTemplate<>(500, "Terjadi kesalahan: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }

    @PostMapping("/calculate")
    public ResponseEntity<?> calculateLoanApplication(HttpServletRequest request, @RequestBody SimulationRequest simulationRequest) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", loanApplicationService.calculateSimulation(userId, simulationRequest)));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getLoanApplicationSummary(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(
                new ResponseTemplate(200, "Success", loanApplicationService.getLoanApplicationSummary(userId))
        );
    }

}
