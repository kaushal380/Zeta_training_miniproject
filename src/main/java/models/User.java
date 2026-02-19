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

    public User() {
    }

    public User(String id, String name, String phoneNumber, String email, LocalDate dateOfBirth, Address address, UserRole role) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String number) {
        this.id = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User ID: " + id +
                "\nName: " + name +
                "\nEmail: " + email +
                "\nRole: " + role;
    }
}
