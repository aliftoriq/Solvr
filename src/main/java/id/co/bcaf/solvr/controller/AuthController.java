package id.co.bcaf.solvr.controller;

import com.google.firebase.auth.FirebaseAuthException;
import id.co.bcaf.solvr.dto.*;
import id.co.bcaf.solvr.dto.auth.*;
import id.co.bcaf.solvr.model.account.User;
import id.co.bcaf.solvr.services.AuthService;
import id.co.bcaf.solvr.services.UserService;
import id.co.bcaf.solvr.utils.JwtUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
        logger.info("Login attempt for username: {}", loginRequest.getUsername());

        LoginResponse loginResponse = authService.authenticateUser(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        logger.info("Successful login for username: {}", loginRequest.getUsername());

        return ResponseEntity.ok(new ResponseTemplate(200, "Success", loginResponse));
    }

    @PostMapping("/login-employee")
    public ResponseEntity<?> loginEmployee(@RequestBody RequestHttpDTO.LoginRequest loginRequest) {
        logger.info("Login attempt for username: {}", loginRequest.getUsername());

        LoginResponse loginResponse = authService.authenticateEmployee(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        logger.info("Successful login for username: {}", loginRequest.getUsername());

        return ResponseEntity.ok(new ResponseTemplate(200, "Success", loginResponse));
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

    @PostMapping("/save-password")
    public ResponseEntity<?> savePsssword(HttpServletRequest request, @RequestBody SavePasswordRequest savePasswordRequest){
        UUID userId = (UUID) request.getAttribute("userId");

        return ResponseEntity
                .ok(new ResponseTemplate(200, "success", authService.savePassword(userId, savePasswordRequest.getPassword())));
    }


//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody User user) {
//
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        User response = userService.createUser(user);
//
//        // Create response DTO using Lombok's setter
//        UserHttp.Response httpResponse = new UserHttp.Response(
//                response.getName(),
//                response.getUsername(),
//                response.getRole().getName(),
//                response.isDeleted()
//        );
//
//        return ResponseEntity
//                .ok(new ResponseTemplate(200, "Success", httpResponse));
//    }


//

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User userRes = authService.registerUser(user);

        // Create response DTO using Lombok's setter
        UserHttp.Response httpResponse = new UserHttp.Response(
                userRes.getName(),
                userRes.getUsername(),
                userRes.getRole().getName(),
                userRes.isDeleted()
        );

        return ResponseEntity.ok(new ResponseTemplate(200, "Verifikasi Email Terkirim", httpResponse));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(new ResponseTemplate(200, "Email verified successfully.", null));
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

    @Secured("AUTH_CHANGE_PASSWORD")
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

    @Secured("AUTH_LOGOUT")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        authService.logout(token);
        return ResponseEntity.ok(new ResponseTemplate(200, "Success", null));
    }

}