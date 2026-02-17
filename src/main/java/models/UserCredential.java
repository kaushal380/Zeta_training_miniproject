package models;

public class UserCredential {

    private String password;
    private User user;

    public UserCredential() {}

    public UserCredential(String password, User user) {
        this.password = password;
        this.user = user;
    }

    public String getPassword() { return password; }
    public User getUser() { return user; }
}
