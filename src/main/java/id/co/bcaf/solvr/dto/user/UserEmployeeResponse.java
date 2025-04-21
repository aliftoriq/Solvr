package id.co.bcaf.solvr.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEmployeeResponse {
    String name;
    String username;
    String role;
    String status;
    boolean deleted = Boolean.FALSE;
    String department;
    String nip;
    String branch;
}
