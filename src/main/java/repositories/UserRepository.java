package repositories;

import models.UserCredential;

import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    private final ConcurrentHashMap<String, UserCredential> users = new ConcurrentHashMap<>();

    public boolean addUser(String userId, UserCredential credential) {
        return users.putIfAbsent(userId, credential) == null;
    }

    public UserCredential getUser(String userId) {
        return users.get(userId);
    }

    public ConcurrentHashMap<String, UserCredential> getAllUsers() {
        return users;
    }
}
