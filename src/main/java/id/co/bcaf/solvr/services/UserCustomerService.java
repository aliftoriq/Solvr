package id.co.bcaf.solvr.services;

import id.co.bcaf.solvr.model.account.User;
import id.co.bcaf.solvr.model.account.UserCustomer;
import id.co.bcaf.solvr.repository.UserCustomerRepository;
import id.co.bcaf.solvr.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserCustomerService {

    @Autowired
    private UserCustomerRepository userCustomerRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public UserCustomer createUserCustomer(UserCustomer userCustomer, UUID userId) {
        User user = userService.getUserById(userId);

        // Simpan UserCustomer terlebih dahulu
        UserCustomer savedCustomer = userCustomerRepository.save(userCustomer);

        // Set ke entity User
        user.setUserCustomer(savedCustomer);
        userService.updateUser(userId ,user);

        return savedCustomer;
    }

    public List<UserCustomer> getAllUserCustomers() {
        return userCustomerRepository.findAll();
    }

    public UserCustomer getUserCustomerById(UUID id) {
        return userCustomerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UserCustomer dengan ID " + id + " tidak ditemukan"));
    }
}

