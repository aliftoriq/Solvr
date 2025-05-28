package id.co.bcaf.solvr.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private long totalEmployees;
    private long totalCustomers;
    private long totalApplications;
    private long approvedApplications;
    private long disbursedApplications;
    private double totalDisbursedAmount;
    private double totalOutstandingAmount;
    private double totalInterestIncome;

    private long applicationsHandledByUser;
    private long applicationsApprovedByUser;
}

