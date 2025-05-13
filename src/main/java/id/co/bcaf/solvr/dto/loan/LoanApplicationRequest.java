package id.co.bcaf.solvr.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequest {
    private double loanAmount;
    private int loanTenor;
    private double longitude;
    private double latitude;
}
