package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.dto.ResponseTemplate;
import id.co.bcaf.solvr.dto.UserHttp;
import id.co.bcaf.solvr.model.account.User;
import id.co.bcaf.solvr.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Secured("USER_READ")
    @GetMapping
    public ResponseTemplate getAllUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            List<User> users = userService.getAllUsers(authHeader);

            List<UserHttp.Response> userResponses = users.stream()
                    .map(user -> new UserHttp.Response(
                            user.getName(),
                            user.getUsername(),
                            user.getRole().getName(),
                            user.isDeleted()
                    ))
                    .collect(Collectors.toList());

            return new ResponseTemplate(200, "Success", userResponses);
        } catch (Exception e) {
            logger.error("Error retrieving users", e);
            return new ResponseTemplate(500, "Internal Server Error", null);
        }
    }

    @Secured("USER_CREATE")
    @PostMapping
    public ResponseTemplate createUser(@RequestBody User user) {
        // Call the service to create the user

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User response = userService.createUser(user);

        // Create response DTO using Lombok's setter
        UserHttp.Response httpResponse = new UserHttp.Response(
                response.getName(),
                response.getUsername(),
                response.getRole().getName(),
                response.isDeleted()
        );

        // Return the newly created user, but only include the non-sensitive information
        return new ResponseTemplate(200, "Success", httpResponse);
    }

    @Secured("USER_UPDATE")
    @PutMapping("{id}")
    public ResponseTemplate updateUser(@PathVariable("id") UUID id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @Secured("USER_DELETE")
    @DeleteMapping("{id}")
    public ResponseTemplate deleteUser(@PathVariable("id") UUID id) {
        return userService.deleteUser(id);
    }
}
