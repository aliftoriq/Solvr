package id.co.bcaf.solvr.services;

import id.co.bcaf.solvr.model.account.LoanApplicationToEmployee;
import id.co.bcaf.solvr.model.account.UserEmployee;
import id.co.bcaf.solvr.repository.LoanApplicationToEmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LoanApplicationToEmployeeService {

    @Autowired
    private LoanApplicationToEmployeeRepository repository;



//    @Transactional
//    public void updateNotes(UUID userId, String notes) {
//        LoanApplicationToEmployee lae = repository.(userId)
//                .orElseThrow(() -> new EntityNotFoundException("LoanApplicationToEmployee tidak ditemukan"));
//
//        lae.setNotes(notes);
//        repository.save(lae);
//    }

   }
