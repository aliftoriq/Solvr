package id.co.bcaf.solvr.dto.loan;


import id.co.bcaf.solvr.dto.plafon.PlafonPackageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanSummaryResponse {
    private String name;
    private double remainingPlafon;
    private double remainingLoan;
    private String accountNumber;
    private double monthlyPayment;

    private List<LoanApplicationResponse> activeLoans;
    private LoanApplicationResponse activeLoanApplication;
    private PlafonPackageResponse plafonPackage;
}
