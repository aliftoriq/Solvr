package id.co.bcaf.solvr.model.services;

import id.co.bcaf.solvr.model.account.Users;
import id.co.bcaf.solvr.repository.UserRepository;
import id.co.bcaf.solvr.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public String authenticateUser(String username, String password) {
        logger.info("Login attempt for : {}", username);
        Optional<Users> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            logger.info("Successful login for user: {}", user.getUsername());
            if (user.getPassword().equals(password)) {
                logger.info("Correct password for ");
                return jwtUtil.generateToken(username);
            }
            else {
                logger.warn("Wrong password for : {}", username);
            }
        }
        else {
            logger.warn("User not found: {}", username);
        }
        return null;
    }
}