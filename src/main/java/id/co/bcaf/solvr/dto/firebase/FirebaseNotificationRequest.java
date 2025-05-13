package id.co.bcaf.solvr.dto.firebase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseNotificationRequest {
    private String title;
    private String body;
    private String token;
}