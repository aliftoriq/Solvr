package id.co.bcaf.solvr.services;

import id.co.bcaf.solvr.config.errorHandler.CustomException;
import id.co.bcaf.solvr.dto.plafon.PlafonPackageResponse;
import id.co.bcaf.solvr.dto.user.UserCustomerDetailResponse;
import id.co.bcaf.solvr.dto.user.UserCustomerRequest;
import id.co.bcaf.solvr.dto.user.UserCustomerResponse;
import id.co.bcaf.solvr.model.account.PlafonPackage;
import id.co.bcaf.solvr.model.account.User;
import id.co.bcaf.solvr.model.account.UserCustomer;
import id.co.bcaf.solvr.repository.PlafonPackageRepository;
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

    @Autowired
    private PlafonPackageService plafonPackageService;

    @Transactional
    public UserCustomer createUserCustomer(UserCustomerRequest userCustomer, UUID userId) {
        User user = userService.getUserById(userId);
        if (user.getUserCustomer() != null) {
            throw new CustomException.UserAlreadyExists("User sudah memiliki UserCustomer");
        }

        UserCustomer addUserCustomer = new UserCustomer();
        addUserCustomer.setNik(userCustomer.getNik());
        addUserCustomer.setAddress(userCustomer.getAddress());
        addUserCustomer.setPhone(userCustomer.getPhone());
        addUserCustomer.setMotherName(userCustomer.getMotherName());
        addUserCustomer.setBirthDate(userCustomer.getBirthDate());
        addUserCustomer.setHousingStatus(userCustomer.getHousingStatus());
        addUserCustomer.setMonthlyIncome(userCustomer.getMonthlyIncome());
        addUserCustomer.setAccountNumber(userCustomer.getAccountNumber());

        addUserCustomer.setTotalPaidLoan(0L);

        PlafonPackage plafonPackage = plafonPackageService.getPlafonPackageByLevel(1);

        addUserCustomer.setPlafonPackage(plafonPackage);

        // Simpan UserCustomer terlebih dahulu
        UserCustomer savedCustomer = userCustomerRepository.save(addUserCustomer);

        // Set ke entity User
        user.setUserCustomer(savedCustomer);
        userService.updateUser(userId ,user);

        return userCustomerRepository.findById(savedCustomer.getId()).orElseThrow(() -> new EntityNotFoundException("UserCustomer dengan ID " + savedCustomer.getId() + " tidak ditemukan"));
    }

    public UserCustomerDetailResponse updateUserCustomer(UUID userId, UserCustomerRequest userCustomer) {
        User user = userService.getUserById(userId);
        if (user.getUserCustomer() == null) {
            throw new EntityNotFoundException("User dengan ID " + userId + " tidak memiliki UserCustomer");
        }

        user.setName(userCustomer.getName());
        userService.updateUser(userId, user);

        UserCustomer userCustomerToUpdate = user.getUserCustomer();

        userCustomerToUpdate.setId(user.getUserCustomer().getId());
        userCustomerToUpdate.setNik(userCustomer.getNik());
        userCustomerToUpdate.setAddress(userCustomer.getAddress());
        userCustomerToUpdate.setPhone(userCustomer.getPhone());
        userCustomerToUpdate.setMotherName(userCustomer.getMotherName());
        userCustomerToUpdate.setBirthDate(userCustomer.getBirthDate());
        userCustomerToUpdate.setHousingStatus(userCustomer.getHousingStatus());
        userCustomerToUpdate.setMonthlyIncome(userCustomer.getMonthlyIncome());
        userCustomerToUpdate.setAccountNumber(userCustomer.getAccountNumber());

        UserCustomer updatedCustomer = userCustomerRepository.save(userCustomerToUpdate);

        UserCustomerDetailResponse userCustomerDetailResponse = new UserCustomerDetailResponse();
        userCustomerDetailResponse.setId(updatedCustomer.getId());
        userCustomerDetailResponse.setName(updatedCustomer.getUser().getName());
        userCustomerDetailResponse.setNik(updatedCustomer.getNik());
        userCustomerDetailResponse.setAddress(updatedCustomer.getAddress());
        userCustomerDetailResponse.setPhone(updatedCustomer.getPhone());
        userCustomerDetailResponse.setMotherName(updatedCustomer.getMotherName());
        userCustomerDetailResponse.setBirthDate(updatedCustomer.getBirthDate());
        userCustomerDetailResponse.setHousingStatus(updatedCustomer.getHousingStatus());
        userCustomerDetailResponse.setMonthlyIncome(updatedCustomer.getMonthlyIncome());
        userCustomerDetailResponse.setTotalPaidLoan(updatedCustomer.getTotalPaidLoan());
        userCustomerDetailResponse.setAccountNumber(updatedCustomer.getAccountNumber());

        PlafonPackageResponse plafonPackage = new PlafonPackageResponse();
        plafonPackage.setId(updatedCustomer.getPlafonPackage().getId());
        plafonPackage.setName(updatedCustomer.getPlafonPackage().getName());
        plafonPackage.setAmount(updatedCustomer.getPlafonPackage().getAmount());
        plafonPackage.setLevel(updatedCustomer.getPlafonPackage().getLevel());
        plafonPackage.setInterestRate(updatedCustomer.getPlafonPackage().getInterestRate());
        plafonPackage.setMaxTenorMonths(updatedCustomer.getPlafonPackage().getMaxTenorMonths());

        userCustomerDetailResponse.setPlafonPackage(plafonPackage);


        return userCustomerDetailResponse;
    }

    public UserCustomerDetailResponse getUserCustomerDetail(UUID userId) {
        User user = userService.getUserById(userId);
        if (user.getUserCustomer() == null) {
            throw new CustomException.UserNotFoundException("User dengan ID " + userId + " tidak memiliki UserCustomer");
        }

        UserCustomerDetailResponse userCustomerDetailResponse = new UserCustomerDetailResponse();
        userCustomerDetailResponse.setId(user.getUserCustomer().getId());
        userCustomerDetailResponse.setName(user.getName());
        userCustomerDetailResponse.setNik(user.getUserCustomer().getNik());
        userCustomerDetailResponse.setAddress(user.getUserCustomer().getAddress());
        userCustomerDetailResponse.setPhone(user.getUserCustomer().getPhone());
        userCustomerDetailResponse.setMotherName(user.getUserCustomer().getMotherName());
        userCustomerDetailResponse.setBirthDate(user.getUserCustomer().getBirthDate());
        userCustomerDetailResponse.setHousingStatus(user.getUserCustomer().getHousingStatus());
        userCustomerDetailResponse.setMonthlyIncome(user.getUserCustomer().getMonthlyIncome());
        userCustomerDetailResponse.setTotalPaidLoan(user.getUserCustomer().getTotalPaidLoan());
        userCustomerDetailResponse.setAccountNumber(user.getUserCustomer().getAccountNumber());

        PlafonPackageResponse plafonPackage = new PlafonPackageResponse();
        plafonPackage.setId(user.getUserCustomer().getPlafonPackage().getId());
        plafonPackage.setName(user.getUserCustomer().getPlafonPackage().getName());
        plafonPackage.setAmount(user.getUserCustomer().getPlafonPackage().getAmount());
        plafonPackage.setLevel(user.getUserCustomer().getPlafonPackage().getLevel());
        plafonPackage.setInterestRate(user.getUserCustomer().getPlafonPackage().getInterestRate());
        plafonPackage.setMaxTenorMonths(user.getUserCustomer().getPlafonPackage().getMaxTenorMonths());

        userCustomerDetailResponse.setPlafonPackage(plafonPackage);

        userCustomerDetailResponse.setUrlProfilePicture(
                user.getUrlProfilePicture() != null ? user.getUrlProfilePicture() : null
        );
        userCustomerDetailResponse.setUrlKtp(
                user.getUserCustomer() != null ? user.getUserCustomer().getUrlKtp() : null
        );
        userCustomerDetailResponse.setUrlSelfieWithKtp(
                user.getUserCustomer() != null ? user.getUserCustomer().getUrlSelfieWithKtp() : null
        );

        return userCustomerDetailResponse;
    }

    public List<UserCustomer> getAllUserCustomers() {
        return userCustomerRepository.findAll();
    }

    public UserCustomer getUserCustomerByUserId(UUID id) {
        User user = userService.getUserById(id);

        return userCustomerRepository.findById(user.getUserCustomer().getId())
                .orElseThrow(() -> new EntityNotFoundException("UserCustomer dengan ID " + id + " tidak ditemukan"));
    }

    public UserCustomer getUserCustomerById(UUID id) {
        return userCustomerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UserCustomer dengan ID " + id + " tidak ditemukan"));
    }

}

