package models;

import models.enums.UserRole;

import java.time.LocalDate;

public class User {

    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private LocalDate dateOfBirth;
    private Address address;
    private UserRole role;

    public User() {}

    public User(String id, String name, String phoneNumber, String email,
                LocalDate dateOfBirth, Address address, UserRole role) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.role = role;
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }

    @Override
    public String toString() {
        return "User ID: " + id +
                "\nName: " + name +
                "\nEmail: " + email +
                "\nRole: " + role;
    }
}
