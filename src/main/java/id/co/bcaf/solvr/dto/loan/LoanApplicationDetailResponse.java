package id.co.bcaf.solvr.dto.loan;

import id.co.bcaf.solvr.dto.plafon.PlafonPackageResponse;
import id.co.bcaf.solvr.dto.user.UserCustomerResponse;
import id.co.bcaf.solvr.dto.user.UserEmployeeResponse;
import id.co.bcaf.solvr.model.account.PlafonPackage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationDetailResponse {
    private UUID id;
    private String name;
    private double loanAmount;
    private int loanTenor;
    private double monthlyPayment;
    private String status;
    private String housingStatus;

    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime disbursedAt;

    private Double longitude;
    private Double latitude;

    private String marketingNotes;
    private String branchManagerNotes;
    private String backOfficeNotes;

    private UserCustomerResponse userCustomer;
    private List<UserEmployeeResponse> userEmployee;

    private PlafonPackageResponse plafonPackage;
    private Double remainingPlafon;
    private Integer totalCompletedLoans;
    private Double totalMonthlyPayment;
    private Double debtToIncomeRatio;
    private String riskLevel;

    private List<LoanApplicationDetailResponse> activeLoans;
    private List<LoanApplicationResponse> loanHistory;

}
