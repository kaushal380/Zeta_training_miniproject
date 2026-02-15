package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import models.Address;
import models.User;
import models.UserCredential;
import models.enums.UserRole;
import repositories.UserRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

public class AuthenticationService {

    private static final String FILE_PATH = "users.json";
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public boolean register(String name,
                            String phone,
                            String email,
                            String password,
                            LocalDate dob,
                            Address address,
                            UserRole role) {

        String userId = UUID.randomUUID().toString();

        User user = new User(userId, name, phone, email, dob, address, role);
        UserCredential credential = new UserCredential(password, user);

        boolean added = userRepository.addUser(userId, credential);

        if (added) {
            saveToFile();
            System.out.println("User registered successfully with ID: " + userId);
            return true;
        }

        System.out.println("Registration failed.");
        return false;
    }

    public User login(String userId, String password) {

        UserCredential credential = userRepository.getUser(userId);

        if (credential == null) {
            System.out.println("User not found.");
            return null;
        }

        if (!credential.getPassword().equals(password)) {
            System.out.println("Invalid password.");
            return null;
        }

        System.out.println("Login successful.");
        return credential.getUser();
    }

    private void saveToFile() {
        try {
            objectMapper.writeValue(new File(FILE_PATH), userRepository.getAllUsers());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
