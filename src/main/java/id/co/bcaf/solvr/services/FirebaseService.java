package id.co.bcaf.solvr.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import id.co.bcaf.solvr.dto.firebase.FirebaseNotificationRequest;
import id.co.bcaf.solvr.model.account.FirebaseToken;
import id.co.bcaf.solvr.model.account.NotificationHistory;
import id.co.bcaf.solvr.model.account.User;
import id.co.bcaf.solvr.model.account.UserCustomer;
import id.co.bcaf.solvr.repository.FirebaseTokenRepository;
import id.co.bcaf.solvr.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class FirebaseService {


    @Autowired
    private FirebaseTokenRepository firebaseTokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCustomerService userCustomerService;

    @Autowired
    private NotificationRepository notificationRepository;

    public String sendNotification(FirebaseNotificationRequest request) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(request.getTitle())
                    .setBody(request.getBody())
                    .build();

            Message message = Message.builder()
                    .setToken(request.getToken())
                    .setNotification(notification)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);

            return "Successfully sent message: " + response;

        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("Failed to send FCM message: " + e.getMessage());
        }
    }

    public String saveToken(UUID userId, FirebaseNotificationRequest request) {
        FirebaseToken firebaseToken = new FirebaseToken();
        firebaseToken.setToken(request.getToken());

        if (userId != null) {
            User user = userService.getUserById(userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found with ID: " + userId);
            }
            firebaseToken.setUser(user);
        } else {
            firebaseToken.setUser(null);
        }

        FirebaseToken tokenTemp = firebaseTokenRepository.findByToken(request.getToken());
        if (tokenTemp != null) {
            firebaseToken.setId(tokenTemp.getId());
        }

        firebaseTokenRepository.save(firebaseToken);
        if (firebaseToken.getId() == null) {
            throw new RuntimeException("Failed to save Firebase token");
        }

        return "Success";
    }


    public String saveNotification(UUID userId, FirebaseNotificationRequest request) {
        User user = userService.getUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        NotificationHistory notification = new NotificationHistory();
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getBody());
        notification.setUser(user);
        notification.setDate(LocalDateTime.now().toString());
        notification.setTime(LocalTime.now().toString());
        notificationRepository.save(notification);

        if (notification.getId() == null) {
            throw new RuntimeException("Failed to save Firebase token");
        }

        return "Success";
    }

    public List<FirebaseToken> getFirebaseToken(UUID userId) {

        return firebaseTokenRepository.findByUser_Id(userId);
    }

    public List<FirebaseToken> getFirebaseTokenByCustomer(UUID userId) {
        UserCustomer userCustomer = userCustomerService.getUserCustomerById(userId);
        if (userCustomer == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        return firebaseTokenRepository.findByUser_Id(userCustomer.getUser().getId());
    }
}
