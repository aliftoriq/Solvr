package id.co.bcaf.solvr.services;

import id.co.bcaf.solvr.model.account.LoanAplicationToEmployee;
import id.co.bcaf.solvr.model.account.LoanApplication;
import id.co.bcaf.solvr.model.account.UserEmployee;
import id.co.bcaf.solvr.repository.LoanApplicationRepository;
import id.co.bcaf.solvr.repository.LoanApplicationToEmployeeRepository;
import id.co.bcaf.solvr.repository.UserEmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LoanApplicationService {
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private LoanApplicationToEmployeeRepository loanAplicationToEmployeeRepository;

    @Autowired
    private UserEmployeeRepository userEmployeeRepository;

    public LoanApplication createApplication(LoanApplication loanApplication) {
        for (LoanAplicationToEmployee lae : loanApplication.getLoanApplicationToEmployees()) {
            // Pastikan setiap entri memiliki LoanApplication yang valid
            lae.setLoanApplication(loanApplication);

            // Pastikan userEmployee berasal dari database (menghindari detached entity)
            UUID userEmployeeId = lae.getUserEmployee().getId();
            UserEmployee userEmployee = userEmployeeRepository.findById(userEmployeeId)
                    .orElseThrow(() -> new RuntimeException("UserEmployee not found: " + userEmployeeId));
            lae.setUserEmployee(userEmployee);
        }

        return loanApplicationRepository.save(loanApplication);
    }

    public List<LoanApplication> getAllApplications() {
        return loanApplicationRepository.findAll();
    }



    @Transactional
    public void marketingApplication(UUID loanAppId, String notes) {
        LoanApplication loanApplication = loanApplicationRepository.findById(loanAppId)
                .orElseThrow(() -> new EntityNotFoundException("LoanApplication dengan ID " + loanAppId + " tidak ditemukan"));

        if (loanApplication.getLoanApplicationToEmployees().isEmpty()) {
            throw new IllegalStateException("LoanApplication dengan ID " + loanAppId + " tidak memiliki pegawai yang terhubung.");
        }

        loanApplication.setStatus("RECOMENDATION");
        loanApplication.getLoanApplicationToEmployees().get(0).setNotes(notes);
        loanApplicationRepository.save(loanApplication);
    }


}
