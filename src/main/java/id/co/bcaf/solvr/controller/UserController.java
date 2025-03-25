package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.dto.ResponseTemplate;
import id.co.bcaf.solvr.model.account.Users;
import id.co.bcaf.solvr.repository.UserRepository;
import id.co.bcaf.solvr.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;

    }

    @GetMapping
    public ResponseTemplate getAllUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract username from token
            String username = extractUsernameFromToken(authHeader);
            logger.info("User {} requested all users list", username);

            List<Users> users = userRepository.findAll();
            return new ResponseTemplate(200, "Success", users);
        } catch (Exception e) {
            logger.error("Error retrieving users", e);
            return new ResponseTemplate(500, "Internal Server Error", null);
        }
    }

    @PostMapping
    public Users createUser(@RequestBody Users user) {

        return userRepository.save(user);
    }

    @PutMapping("{id}")
    public ResponseTemplate updateUser(@PathVariable("id") UUID id, @RequestBody Users user) {
        user.setId(id);
        return new ResponseTemplate(200, "Success", userRepository.save(user));
    }

    @DeleteMapping("{id}")
    public ResponseTemplate deleUsers(@PathVariable("id") UUID id){

        Users user = userRepository.getById(id);
        userRepository.delete(user);

        return new ResponseTemplate(200, "Success Deleted Data "+ id, null);
    }

    // Helper method to extract username from token
    private String extractUsernameFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String token = authHeader.substring(7);
        return jwtUtil.extractusername(token);
    }

}
