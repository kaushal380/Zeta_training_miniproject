package services;

import java.util.concurrent.ConcurrentHashMap;

public class AuthenticationService {

    private ConcurrentHashMap<String, String> usernamePasswords = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, String> getUsernamePasswords() {
        return usernamePasswords;
    }

    public boolean login(String username, String password){

        return true;
    }

    public boolean register(String username, String password){

        return true;
    }


}
