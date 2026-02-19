package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import models.Address;
import models.User;
import models.UserCredential;
import models.enums.UserRole;
import repositories.UserRepository;

import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Logger;

public class AuthenticationService {

    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());
    private static final String FILE_PATH = "users.json";

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public User register(String name,
                         String phone,
                         String email,
                         String password,
                         LocalDate dob,
                         Address address,
                         UserRole role) {

        String userId = UUID.randomUUID().toString();
        User user = new User(userId, name, phone, email, dob, address, role);
        UserCredential credential = new UserCredential(password, user);

        boolean added = userRepository.addUser(email, credential);

        if (added) {
            logger.info("User registered successfully with email: " + email);
            return user;
        }

        logger.warning("Registration failed for email: " + email);
        return null;
    }

    public User login(String email, String password) {

        UserCredential credential = userRepository.getUser(email);

        if (credential == null) {
            logger.warning("Login failed - User not found: " + email);
            return null;
        }

        if (!credential.getPassword().equals(password)) {
            logger.warning("Login failed - Invalid password for: " + email);
            return null;
        }

        logger.info("Login successful for: " + email);
        return credential.getUser();
    }

}
