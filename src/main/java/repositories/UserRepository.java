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

    private static final Logger logger =
            Logger.getLogger(UserRepository.class.getName());

    private final String filePath;
    private final Map<String, UserCredential> users =
            new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    public UserRepository(String file_path) {

        this.filePath = file_path;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        loadFromFile();
    }

    public UserRepository(){
        this("users.json");
    }

    public boolean addUser(String email, UserCredential credential) {

        if (users.containsKey(email)) {
            logger.warning("Attempt to register duplicate email: " + email);
            return false;
        }

        users.put(email, credential);
        logger.info("User added to repository: " + email);

        saveToFile();
        return true;
    }

    public UserCredential getUser(String email) {
        return users.get(email);
    }

    public Map<String, UserCredential> getAllUsers() {
        return users;
    }

    private void loadFromFile() {

        File file = new File(filePath);

        if (!file.exists()) {
            logger.info("File not found. Starting with empty repository.");
            return;
        }

        if (file.length() == 0) {
            logger.warning("File exists but is empty. Starting with empty repository.");
            return;
        }


        try {
            Map<String, UserCredential> fileUsers =
                    objectMapper.readValue(
                            file,
                            new TypeReference<Map<String, UserCredential>>() {}
                    );

            users.putAll(fileUsers);

            logger.info("Loaded " + fileUsers.size() + " users from file.");

        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    "Failed to load users from file: " + filePath, e);

            throw new RuntimeException("UserRepository initialization failed", e);
        }
    }

    private void saveToFile() {

        try {
            objectMapper.writeValue(new File(filePath), users);
            logger.info("Users saved successfully to file.");

        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    "Failed to save users to file: " + filePath, e);

            throw new RuntimeException("Failed to persist users", e);
        }
    }
}
