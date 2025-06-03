package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.dto.ResponseHttpDTO;
import id.co.bcaf.solvr.dto.ResponseTemplate;
import id.co.bcaf.solvr.dto.UserHttp;
import id.co.bcaf.solvr.dto.feature.ManyRoleToFeatureRequest;
import id.co.bcaf.solvr.dto.feature.RoleToFeatureRequest;
import id.co.bcaf.solvr.dto.role.FeatureResponse;
import id.co.bcaf.solvr.model.account.Feature;
import id.co.bcaf.solvr.model.account.RoleToFeature;
import id.co.bcaf.solvr.services.FeatureService;
import id.co.bcaf.solvr.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/feature")
public class FeatureController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private FeatureService featureService;

    @Secured("FEATURE_READ")
    @GetMapping
    public ResponseEntity<?> getFeature() {

        List<FeatureResponse> features = featureService.getFeature();

        return ResponseEntity.ok(new ResponseTemplate(200, "Success", features));
    }

    @Secured("FEATURE_CREATE")
    @PostMapping
    public ResponseEntity<?> createFeature(@RequestBody Feature feature) {
        return ResponseEntity.ok(featureService.createFeature(feature));
    }

    @Secured("ROLE_FEATURE_READ")
    @GetMapping("/role-to-feature")
    public ResponseEntity<?> getAllRoleToFeature() {
        return ResponseEntity.ok(featureService.getAllRoleToFeature());
    }

    @Secured("ROLE_FEATURE_CREATE")
    @PostMapping("/role-to-feature")
    public ResponseEntity<?> createRoleToFeature(@RequestBody RoleToFeatureRequest roleToFeature) {
        return ResponseEntity.ok(featureService.createRoleToFeature(roleToFeature));
    }

    @Secured("ROLE_FEATURE_CREATE_MANY")
    @PostMapping("/role-to-feature/many")
    public ResponseEntity<?> createRoleToFeatureMany(@RequestBody ManyRoleToFeatureRequest roleToFeature) {
        featureService.createRoleToFeatureMany(roleToFeature);
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", null));
    }

    @Secured("ROLE_FEATURE_DELETE_MANY")
    @DeleteMapping("/role-to-feature/many")
    public ResponseEntity<?> deleteRoleToFeatureMany(@RequestBody ManyRoleToFeatureRequest roleToFeature) {
        featureService.deleteRoleToFeatureMany(roleToFeature);
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", null));
    }
}
