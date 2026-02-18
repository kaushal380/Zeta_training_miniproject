package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import models.UserCredential;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRepository {

    private static final Logger logger = Logger.getLogger(UserRepository.class.getName());

    private final String filePath;

    private final Map<String, UserCredential> users = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    private final Object lock = new Object();

    public UserRepository(String filePath) {

        this.filePath = filePath;

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        loadFromFile();
    }

    public UserRepository() {
        this("users.json");
    }

    public boolean addUser(String email, UserCredential credential) {

        synchronized (lock) {

            if (users.containsKey(email)) {
                logger.warning("Attempt to register duplicate email: " + email);
                return false;
            }

            users.put(email, credential);
            saveToFile();

            logger.info("User added to repository: " + email);
            return true;
        }
    }

    public UserCredential getUser(String email) {
        return users.get(email);
    }

    public Map<String, UserCredential> getAllUsers() {
        return users;
    }

    private void loadFromFile() {

        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            logger.info("File not found or empty. Starting clean.");
            return;
        }

        synchronized (lock) {
            try {
                Map<String, UserCredential> fileUsers = objectMapper.readValue(file, new TypeReference<Map<String, UserCredential>>() {
                });

                users.putAll(fileUsers);

                logger.info("Loaded " + fileUsers.size() + " users.");

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to load users from file: " + filePath, e);

                throw new RuntimeException("UserRepository initialization failed", e);
            }
        }
    }

    private void saveToFile() {

        try {
            objectMapper.writeValue(new File(filePath), users);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save users to file: " + filePath, e);

            throw new RuntimeException("Failed to persist users", e);
        }
    }
}