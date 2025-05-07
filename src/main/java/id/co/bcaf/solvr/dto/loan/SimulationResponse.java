package id.co.bcaf.solvr.dto.loan;

import lombok.Data;

@Data
public class SimulationResponse {
    private Double monthlyInstallment;
    private Double totalInterest;
    private Double totalPayment;
}

