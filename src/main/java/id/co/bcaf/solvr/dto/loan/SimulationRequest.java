package id.co.bcaf.solvr.dto.loan;

import lombok.Data;

@Data
public class SimulationRequest {
    private double loanAmount;
    private Integer loanTenor;
}
