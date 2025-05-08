package id.co.bcaf.solvr.dto.plafon;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlafonPackageRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Amount is required")
    private Long amount;

    @NotBlank(message = "Level is required")
    private Integer level;

    @NotBlank(message = "Interest rate is required")
    private Double interestRate;

    @NotBlank(message = "Max tenor months is required")
    private Integer maxTenorMonths;
}
