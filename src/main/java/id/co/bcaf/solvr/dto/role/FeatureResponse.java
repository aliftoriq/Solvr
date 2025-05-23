package id.co.bcaf.solvr.dto.role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureResponse {
    private UUID id;
    private String name;
}
