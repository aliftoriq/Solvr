package id.co.bcaf.solvr.controller;

import com.google.firebase.auth.FirebaseAuthException;
import id.co.bcaf.solvr.dto.*;
import id.co.bcaf.solvr.dto.auth.ChangePasswordRequest;
import id.co.bcaf.solvr.dto.auth.ForgetPasswordRequest;
import id.co.bcaf.solvr.dto.auth.LoginResponse;
import id.co.bcaf.solvr.dto.auth.ResetPasswordRequest;
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

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

//TEST CI CD

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
    public ResponseEntity<?> login(@RequestBody RequestHttpDTO.LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for username: {}", loginRequest.getUsername());

            LoginResponse loginResponse = authService.authenticateUser(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            if (loginResponse.getToken() == null) {
                logger.warn("Authentication failed for username: {}", loginRequest.getUsername());
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseTemplate(401, "Invalid username or password", null));
            }

            logger.info("Successful login for username: {}", loginRequest.getUsername());
            return ResponseEntity.ok(new ResponseTemplate(200, "Success", loginResponse));


        } catch (Exception e) {
            logger.error("Login error", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate(500, "Internal Server Error", null));
        }
    }


    @PostMapping("/firebase-login")
    public ResponseEntity<?> firebaseLogin(@RequestBody RequestHttpDTO.LoginFirebaseRequest payload) {
        String token = payload.getToken();

        try {
            LoginResponse loginResponse = authService.authenticateUserFromFirebaseToken(token);
            return ResponseEntity.ok(new ResponseTemplate(200, "Success", loginResponse));
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseTemplate(401, "Invalid Firebase Token", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate(500, "Internal Server Error", e.getMessage()));
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User response = userService.createUser(user);

        // Create response DTO using Lombok's setter
        UserHttp.Response httpResponse = new UserHttp.Response(
                response.getName(),
                response.getUsername(),
                response.getRole().getName(),
                response.isDeleted()
        );

        return ResponseEntity
                .ok(new ResponseTemplate(200, "Success", httpResponse));


    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        try {
            String response = authService.sendPasswordResetLink(forgetPasswordRequest.getUsername());
            return ResponseEntity.ok(new ResponseTemplate(200, "Success", response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTemplate(500, "Internal Server Error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        String result = authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", result));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordRequest request) {
        try {
            String rawToken = token.replace("Bearer ", "");
            UUID userId = jwtUtil.extractId(rawToken);

            String result = authService.changePassword(userId, request.getOldPassword(), request.getNewPassword());

            return ResponseEntity.ok(new ResponseTemplate(200, "Success", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ResponseTemplate(400, "Password Lama Salah", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseTemplate(404, "Not Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate(500, "Internal Server Error", "Terjadi kesalahan saat memproses permintaan."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        authService.logout(token);
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", null));
    }

}