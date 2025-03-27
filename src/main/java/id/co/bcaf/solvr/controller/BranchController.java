package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.model.account.Branch;
import id.co.bcaf.solvr.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/branch")
public class BranchController {
    @Autowired
    BranchRepository branchRepository;

    @GetMapping
    public ResponseEntity<?> getAllBranch() {
        return ResponseEntity.ok(branchRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createBranch(@RequestBody Branch branch) {
        return ResponseEntity.ok(branchRepository.save(branch));
    }
}
