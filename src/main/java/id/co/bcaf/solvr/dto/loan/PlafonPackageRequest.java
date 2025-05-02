package id.co.bcaf.solvr.dto.loan;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlafonPackageRequest {
    private String name;
    private Long amount;
    private Integer level;
    private Double interestRate;
    private Integer maxTenorMonths;
}
