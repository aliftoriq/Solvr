package id.co.bcaf.solvr.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import id.co.bcaf.solvr.config.errorHandler.CustomException;
import id.co.bcaf.solvr.dto.UserResponse;
import id.co.bcaf.solvr.dto.auth.LoginResponse;
import id.co.bcaf.solvr.model.account.*;
import id.co.bcaf.solvr.repository.PasswordTokenRepository;
import id.co.bcaf.solvr.repository.UserRepository;
import id.co.bcaf.solvr.utils.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private PasswordTokenRepository resetTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private BlacklistTokenService blacklistTokenService;
    @Autowired
    private FeatureService featureService;
    @Autowired
    private RoleService roleService;


    @Transactional
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new CustomException.UserAlreadyExists("Email sudah terdaftar.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(false);

        if (user.getRole() == null) {
            Role defaultRole = new Role();
            defaultRole.setId(2);
            user.setRole(defaultRole);
        }

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateVerificationToken(savedUser.getUsername());

        String hashedToken = passwordEncoder.encode(token);
        savedUser.setVerifyTokenHash(hashedToken);
        userRepository.save(savedUser);

        String verifyUrl = "https://solvr-web.vercel.app/verify-email?token=" + token;

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String htmlContent = "<div style=\"font-family: Arial, sans-serif; font-size: 16px; color: #333; padding: 20px;\">" +
                    "<h2 style=\"color: #2b8a3e;\">Verifikasi Akun Anda</h2>" +
                    "<p>Halo <strong>" + user.getName() + "</strong>,</p>" + // Jika user.getName() tersedia
                    "<p>Terima kasih telah mendaftar di <strong>Solvr</strong>.</p>" +
                    "<p>Untuk mengaktifkan akun Anda, silakan klik tombol di bawah ini:</p>" +
                    "<a href=\"" + verifyUrl + "\" style=\"display: inline-block; padding: 10px 20px; background-color: #2b8a3e; color: white; text-decoration: none; border-radius: 5px;\">Verifikasi Akun</a>" +
                    "<p style=\"margin-top: 20px;\">Tautan ini berlaku selama 15 menit.</p>" +
                    "<hr style=\"margin-top: 30px;\">" +
                    "<p style=\"font-size: 12px; color: #999;\">Jika Anda tidak merasa melakukan pendaftaran, silakan abaikan email ini.</p>" +
                    "</div>";

            helper.setTo(user.getUsername());
            helper.setSubject("Verifikasi Akun Solvr Anda");
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(mimeMessage);
            logger.info("Email verifikasi berhasil dikirim ke: {}", user.getUsername());

        } catch (MessagingException e) {
            logger.error("Gagal mengirim email verifikasi", e);
            throw new RuntimeException("Gagal mengirim email verifikasi");
        }

        return userService.getUserById(savedUser.getId());
    }

    @Transactional
    public void verifyEmail(String token) {
        Optional<User> userOptional = userRepository.findAll().stream()
                .filter(user -> user.getVerifyTokenHash() != null && passwordEncoder.matches(token, user.getVerifyTokenHash()))
                .findFirst();

        if (userOptional.isEmpty()) {
            throw new CustomException.InvalidInputException("Invalid or expired verification token.");
        }

        User user = userOptional.get();

        if (user.isVerified()) {
            throw new CustomException.InvalidInputException("User already verified.");
        }

        user.setVerified(true);
        user.setVerifyTokenHash(null);
        userRepository.save(user);
    }


    public LoginResponse authenticateUser(String username, String password) {
        logger.info("Login attempt for : {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException.UserNotFoundException("User dengan email tersebut tidak ditemukan."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Wrong password for: {}", username);
            throw new CustomException.InvalidInputException("Username atau password salah.");
        }

        logger.info("Successful login for user: {}", user.getUsername());

        List<RoleToFeature> roleToFeatures = featureService.getRoleToFeatureByRole(user.getRole());

        List<String> features = roleToFeatures.stream()
                .map(RoleToFeature::getFeature)
                .map(Feature::getName)
                .collect(Collectors.toList());

        return new LoginResponse(
                jwtUtil.generateToken(username, user.getRole().getName(), user.getId()),
                features,
                new UserResponse(
                        user.getName(),
                        user.getUsername(),
                        user.getRole().getName(),
                        user.getStatus(),
                        user.isVerified(),
                        user.isDeleted()
                )
        );
    }

    public LoginResponse authenticateEmployee(String username, String password) {
        logger.info("Login attempt for : {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException.UserNotFoundException("User dengan email tersebut tidak ditemukan."));

        if(user.getRole().getName().equals("CUSTOMER")) {
            throw new CustomException.UserNotFoundException("User dengan email tersebut tidak ditemukan.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Wrong password for: {}", username);
            throw new CustomException.InvalidInputException("Username atau password salah.");
        }

        logger.info("Successful login for user: {}", user.getUsername());

        List<RoleToFeature> roleToFeatures = featureService.getRoleToFeatureByRole(user.getRole());

        List<String> features = roleToFeatures.stream()
                .map(RoleToFeature::getFeature)
                .map(Feature::getName)
                .collect(Collectors.toList());

        return new LoginResponse(
                jwtUtil.generateToken(username, user.getRole().getName(), user.getId()),
                features,
                new UserResponse(
                        user.getName(),
                        user.getUsername(),
                        user.getRole().getName(),
                        user.getStatus(),
                        user.isVerified(),
                        user.isDeleted()
                )
        );
    }


    public LoginResponse authenticateUserFromFirebaseToken(String firebaseToken) throws FirebaseAuthException {
        logger.info("Authenticating Firebase token...");

        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
        String email = decodedToken.getEmail();
        String name = decodedToken.getName();

        logger.info("Firebase token verified for email: {}", email);

        Optional<User> userOptional = userRepository.findByUsername(email);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            logger.info("User found: {}", user.getUsername());
        } else {
            // Buat user baru jika belum ada
            user = new User();
            user.setUsername(email);
            user.setName(name != null ? name : "User");
            user.setPassword("");
            user.setStatus("needs_password");

            Role role = roleService.getRoleByName("CUSTOMER");
            user.setRole(role);
            user.setVerified(true);
            user = userRepository.save(user);

            logger.info("New user created from Firebase token: {}", user.getUsername());
        }

        // Cek apakah password kosong
        String status = (user.getPassword() == null || user.getPassword().isEmpty())
                ? "needs_password"
                : user.getStatus();

        List<RoleToFeature> roleToFeatures = featureService.getRoleToFeatureByRole(user.getRole());

        List<String> features = roleToFeatures.stream()
                .map(RoleToFeature::getFeature)
                .map(Feature::getName)
                .collect(Collectors.toList());

        return new LoginResponse(
                jwtUtil.generateToken(user.getUsername(), user.getRole().getName(), user.getId()),
                features,
                new UserResponse(user.getName(), user.getUsername(), user.getRole().getName(), status, user.isVerified(), user.isDeleted())
        );
    }

    public String savePassword(UUID userId, String password){
        User user = userService.getUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        if (Objects.equals(user.getStatus(), "needs_password")){
            user.setPassword(passwordEncoder.encode(password));

            user.setStatus("ACTIVE");
            userService.updateUser(user.getId(), user);
            return "Password berhasil disimpan";
        } else {
            throw new CustomException.InvalidInputException("User sudah memiliki password");
        }
    }


    @Transactional
    public String sendPasswordResetLink(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new NoSuchElementException("User not found.");
        }

        User user = userOptional.get();

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        if (resetTokenRepository.findByUser(user).isPresent()) {
            resetTokenRepository.deleteByUserId(user.getId());
        }

        PasswordToken resetToken = new PasswordToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiry);

        resetTokenRepository.save(resetToken);

        String resetUrl = "https://solvr-web.vercel.app/reset-password?token=" + token;

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String htmlContent = "<div style=\"font-family: Arial, sans-serif; font-size: 16px; color: #333; padding: 20px;\">" +
                    "<h2 style=\"color: #2b8a3e;\">Reset Password Request</h2>" +
                    "<p>Hello <strong>" + user.getName() + "</strong>,</p>" +
                    "<p>We received a request to reset your password.</p>" +
                    "<p>Please click the button below to reset your password:</p>" +
                    "<a href=\"" + resetUrl + "\" style=\"display: inline-block; padding: 10px 20px; background-color: #2b8a3e; color: white; text-decoration: none; border-radius: 5px;\">Reset Password</a>" +
                    "<p style=\"margin-top: 20px;\">This link will expire in 15 minutes.</p>" +
                    "<hr style=\"margin-top: 30px;\">" +
                    "<p style=\"font-size: 12px; color: #999;\">If you didn't request a password reset, please ignore this email.</p>" +
                    "</div>";

            helper.setTo(user.getUsername());
            helper.setSubject("Reset Password Request");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            logger.info("Password reset email sent to: {}", user.getUsername());

        } catch (MessagingException e) {
            logger.error("Failed to send email", e);
            throw new RuntimeException("Failed to send email");
        }

        logger.info("Password reset link sent to: {}", user.getUsername());

        return "Reset link sent to your email.";
    }

    @Transactional
    public String resetPassword(String token, String newPassword) {
        Optional<PasswordToken> tokenOptional = resetTokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()) {
            logger.warn("Invalid or expired token.");
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        PasswordToken resetToken = tokenOptional.get();

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            resetTokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Token expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetTokenRepository.delete(resetToken);

        return "Password reset successfully.";
    }

    @Transactional
    public String changePassword(UUID userId, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new NoSuchElementException("User dengan ID " + userId + " tidak ditemukan.");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Password lama salah.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password berhasil diubah.";
    }


    public void logout(String token) {
        blacklistTokenService.blacklistToken(token);
    }


//    public  String getFeature(String username) {
//        Optional<User> userOptional = userRepository.findByUsername(username);
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//
//            Optional
//
//
//            return ""
//        }
//        return null;
//    }
}