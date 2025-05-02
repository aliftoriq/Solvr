package id.co.bcaf.solvr.dto.feature;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter @Setter
public class RoleToFeatureRequest {
    private int roleId;
    private UUID featureId;
}
