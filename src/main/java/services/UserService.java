package services;

import models.User;
import models.UserCredential;
import models.enums.UserRole;
import repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User getUserByEmail(String email) {
        UserCredential credential = userRepository.getUser(email);
        return credential != null ? credential.getUser() : null;
    }


    public List<User> getUsersByRole(UserRole role) {

        List<User> result = new ArrayList<>();

        Map<String, UserCredential> allUsers = userRepository.getAllUsers();

        for (UserCredential credential : allUsers.values()) {

            User user = credential.getUser();

            if (user.getRole() == role) {
                result.add(user);
            }
        }

        return result;
    }

    public List<User> getAllUsers(User admin) {

        if (admin.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only admin can view all the users");
        }

        List<User> userList = new ArrayList<>();

        Map<String, UserCredential> allUsers = userRepository.getAllUsers();

        for (UserCredential credential : allUsers.values()) {

            User user = credential.getUser();
            userList.add(user);
        }

        return userList;
    }


    public boolean emailExists(String email) {
        return userRepository.getUser(email) != null;
    }


    public boolean deleteUser(String email, User user) {

        if (user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only admin can delete a user");
        }

        Map<String, UserCredential> users = userRepository.getAllUsers();

        if (!users.containsKey(email)) {
            return false;
        }

        users.remove(email);
        return true;
    }

}
