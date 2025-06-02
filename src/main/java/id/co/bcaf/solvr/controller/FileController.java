package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.dto.ResponseTemplate;
import id.co.bcaf.solvr.services.CloudinaryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/image")
public class FileController {
    @Autowired
    private CloudinaryService cloudinaryService;

    @Secured("IMAGE_UPLOAD")
    @PostMapping
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String url = cloudinaryService.uploadImage(file);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    @Secured("IMAGE_PROFILE_UPLOAD")
    @PostMapping("/profile")
    public ResponseEntity<?> uploadProfile(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        UUID userId = (UUID) request.getAttribute("userId");
        try {
            String url = cloudinaryService.uploadProfileImage(userId, file);
            return ResponseEntity.ok(new ResponseTemplate(200, "Success", url));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfilePicture(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        try {
            String url = cloudinaryService.getUserProfilePicture(userId);
            return ResponseEntity.ok(new ResponseTemplate(200, "Success", url));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    @Secured("IMAGE_KTP_UPLOAD")
    @PostMapping("/ktp")
    public ResponseEntity<?> uploadKtp(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        UUID userId = (UUID) request.getAttribute("userId");
        try {
            String url = cloudinaryService.uploadKtp(userId, file);
            return ResponseEntity.ok(new ResponseTemplate(200, "Success", url));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    @Secured("IMAGE_SELFIE_UPLOAD")
    @PostMapping("/selfie")
    public ResponseEntity<?> uploadSelfie(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        UUID userId = (UUID) request.getAttribute("userId");
        try {
            String url = cloudinaryService.uploadSelfie(userId, file);
            return ResponseEntity.ok(new ResponseTemplate(200, "Success", url));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
}
