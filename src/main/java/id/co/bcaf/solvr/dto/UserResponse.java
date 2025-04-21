package id.co.bcaf.solvr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String name;
    private String username;
    private String role;
    private String status;
    private boolean deleted = Boolean.FALSE;
}
