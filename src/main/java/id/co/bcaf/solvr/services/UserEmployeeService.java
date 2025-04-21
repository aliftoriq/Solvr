package id.co.bcaf.solvr.services;

import id.co.bcaf.solvr.dto.user.UserEmployeeResponse;
import id.co.bcaf.solvr.model.account.User;
import id.co.bcaf.solvr.model.account.UserEmployee;
import id.co.bcaf.solvr.repository.UserEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;

@Service
public class UserEmployeeService {
    @Autowired
    private UserEmployeeRepository userEmployeeRepository;

    @Autowired
    private UserService userService;

    public UserEmployee createUserEmployee(UUID userId, UserEmployee userEmployee) {
        User user = userService.getUserById(userId);

        UserEmployee savedUserEmployee = userEmployeeRepository.save(userEmployee);

        user.setUserEmployee(userEmployee);
        userService.updateUser(userId, user);

        return savedUserEmployee;
    }

    public UserEmployeeResponse getUserEmployeeByUserId(UUID userId) {
        User user = userService.getUserById(userId);

        if (user.getUserEmployee() == null) {
            throw new IllegalArgumentException("User ini bukan karyawan/employee");
        }

        UserEmployeeResponse userEmployeeResponse = new UserEmployeeResponse();
        userEmployeeResponse.setName(user.getName());
        userEmployeeResponse.setUsername(user.getUsername());
        userEmployeeResponse.setRole(user.getRole().getName());
        userEmployeeResponse.setStatus(user.getStatus());
        userEmployeeResponse.setDeleted(user.isDeleted());
        userEmployeeResponse.setDepartment(user.getUserEmployee().getDepartment());
        userEmployeeResponse.setNip(user.getUserEmployee().getNip());
        userEmployeeResponse.setBranch(user.getUserEmployee().getBranch().getName());


        return userEmployeeResponse;
    }

    public UserEmployee updateUserEmployee(UUID userId, UserEmployee userEmployee) {
        User user = userService.getUserById(userId);
        UserEmployee savedUserEmployee = userEmployeeRepository.save(userEmployee);
        user.setUserEmployee(userEmployee);
        userService.updateUser(userId, user);
        return savedUserEmployee;
    }

    public void deleteUserEmployee(UUID userId) {
        User user = userService.getUserById(userId);
        user.setUserEmployee(null);
        userService.updateUser(userId, user);
    }

    public List<UserEmployee> getAllUserEmployee() {
        return userEmployeeRepository.findAll();
    }

}
