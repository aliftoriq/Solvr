package id.co.bcaf.solvr.dto.user;

import id.co.bcaf.solvr.dto.branch.BranchResponse;
import id.co.bcaf.solvr.dto.role.RoleResponse;
import id.co.bcaf.solvr.model.account.Branch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEmployeeResponse {
    UUID id;
    String name;
    String username;
    String status;
    String email;
    boolean deleted = Boolean.FALSE;
    String department;
    String nip;
    RoleResponse role;
    BranchResponse branch;
}
