package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.dto.LoginRequest;
import id.co.bcaf.solvr.dto.ResponseTemplate;
import id.co.bcaf.solvr.dto.UserHttp;
import id.co.bcaf.solvr.model.account.User;
import id.co.bcaf.solvr.services.AuthService;
import id.co.bcaf.solvr.services.UserService;
import id.co.bcaf.solvr.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final UserService userService;

    // Constructor injection
    public AuthController(JwtUtil jwtUtil, AuthService authService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for username: {}", loginRequest.getUsername());

            String token = authService.authenticateUser(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            if (token == null) {
                logger.warn("Authentication failed for username: {}", loginRequest.getUsername());
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseTemplate(401, "Invalid username or password", null));
            }

            logger.info("Successful login for username: {}", loginRequest.getUsername());
            return ResponseEntity.ok(new ResponseTemplate(200, "Success", token));
        } catch (Exception e) {
            logger.error("Login error", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate(500, "Internal Server Error", null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Call the service to create the user

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try{
            User response = userService.createUser(user);

            // Create response DTO using Lombok's setter
            UserHttp.Response httpResponse = new UserHttp.Response(
                    response.getName(),
                    response.getUsername(),
                    response.getRole().getName(),
                    response.isDeleted()
            );

            // Return the newly created user, but only include the non-sensitive information
            return ResponseEntity
                    .ok(new ResponseTemplate(200, "Success", httpResponse));
        }catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTemplate(500, "Internal Server Error", e.getMessage()));

        }


    }

}