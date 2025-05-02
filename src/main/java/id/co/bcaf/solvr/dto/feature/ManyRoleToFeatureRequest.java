package id.co.bcaf.solvr.dto.feature;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManyRoleToFeatureRequest {
    private int roleId;
    private List<UUID> listFeatureId;
}
