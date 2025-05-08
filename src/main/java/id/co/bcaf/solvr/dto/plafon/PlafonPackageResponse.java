package id.co.bcaf.solvr.dto.plafon;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlafonPackageResponse {
    private Long id;

    private String name;
    private Long amount;
    private Integer level;

    private Double interestRate;
    private Integer maxTenorMonths;
}
