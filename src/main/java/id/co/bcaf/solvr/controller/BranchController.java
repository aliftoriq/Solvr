package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.dto.ResponseTemplate;
import id.co.bcaf.solvr.model.account.Branch;
import id.co.bcaf.solvr.repository.BranchRepository;
import id.co.bcaf.solvr.services.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/branch")
public class BranchController {
    @Autowired
    BranchRepository branchRepository;

    @Autowired
    BranchService branchService;

    @GetMapping
    public ResponseEntity<?> getAllBranch() {
        return ResponseEntity.ok(branchRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createBranch(@RequestBody Branch branch) {
        return ResponseEntity.ok(branchRepository.save(branch));
    }

    @PostMapping("/nearest")
    public ResponseEntity<?> getNearestBranch(@RequestBody Branch branch) {
        return ResponseEntity.ok(branchService.getNearestBranch(branch.getLatitude(), branch.getLongitude()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBranch(@PathVariable UUID id, @RequestBody Branch branch) {
        branch.setId(id);
        return ResponseEntity.ok(branchRepository.save(branch));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBranch(@PathVariable UUID id) {
        branchService.deleteBranch(id);

        return ResponseEntity.ok(new ResponseTemplate(200, "Success", null));
    }
}
