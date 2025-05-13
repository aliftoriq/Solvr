package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.dto.ResponseTemplate;
import id.co.bcaf.solvr.dto.firebase.FirebaseNotificationRequest;
import id.co.bcaf.solvr.services.FirebaseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping
    public ResponseEntity<?> saveToken(HttpServletRequest request, @RequestBody FirebaseNotificationRequest notificationRequest) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", firebaseService.saveToken(userId, notificationRequest)));
    }


    @PostMapping("/test")
    public ResponseEntity<?> sendNotification(@RequestBody FirebaseNotificationRequest request) {
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", firebaseService.sendNotification(request)));
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveNotification(HttpServletRequest request, @RequestBody FirebaseNotificationRequest notificationRequest) {
        UUID userId = (UUID) request.getAttribute("userId");
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", firebaseService.saveNotification(userId, notificationRequest)));
    }
}
