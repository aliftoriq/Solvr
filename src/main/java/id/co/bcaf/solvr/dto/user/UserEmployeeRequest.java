package id.co.bcaf.solvr.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEmployeeRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "NIP is required")
    private String nip;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Department is required")
    private String department;

    @NotNull(message = "Branch ID is required")
    private UUID branchId;

    @NotNull(message = "Role ID is required")
    @Positive(message = "Role ID must be a positive integer")
    private Integer roleId;
}
