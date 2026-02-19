package consoleUI;

import models.Address;
import models.User;
import models.enums.UserRole;
import services.AuthenticationService;

import java.time.LocalDate;
import java.util.Scanner;

import static consoleUI.inputValidators.InputValidator.getValidChoice;

public class AuthenticationDashboard {

    public static User handleLogin(AuthenticationService authService, Scanner scanner) {

        System.out.println("\n========== LOGIN ==========");

        String email;
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine().trim();

            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
                break;

            System.out.println("That doesn't look like a valid email.");
        }

        String password;
        while (true) {
            System.out.print("Password: ");
            password = scanner.nextLine().trim();

            if (!password.isEmpty())
                break;

            System.out.println("Password cannot be empty.");
        }

        User user = authService.login(email, password);

        if (user != null) {
            System.out.println("Welcome back, " + user.getName() + "!");
            return user;
        }

        System.out.println("Incorrect email or password.");
        return null;
    }

    public static User handleRegister(AuthenticationService authService, Scanner scanner, boolean admin) {

        System.out.println("\n========== REGISTER USER ==========");

        String name;
        while (true) {
            System.out.print("Full Name: ");
            name = scanner.nextLine().trim();

            if (name.matches("[a-zA-Z ]+"))
                break;

            System.out.println("Name should contain only alphabets.");
        }

        String phone;
        while (true) {
            System.out.print("Phone Number: ");
            phone = scanner.nextLine().trim();

            if (phone.matches("[6-9][0-9]{9}"))
                break;

            System.out.println("Enter a valid 10-digit Indian number starting with 6-9.");
        }

        String email;
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine().trim();

            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
                break;

            System.out.println("Enter a valid email (example: abc@xyz.com).");
        }

        String password;
        while (true) {
            System.out.print("Password: ");
            password = scanner.nextLine().trim();

            if (!password.isEmpty())
                break;

            System.out.println("Password cannot be empty.");
        }

        LocalDate dob;
        while (true) {
            try {
                System.out.print("Date of Birth (YYYY-MM-DD): ");
                dob = LocalDate.parse(scanner.nextLine().trim());

                if (dob.isBefore(LocalDate.now()))
                    break;

                System.out.println("Date of birth must be in the past.");

            } catch (Exception e) {
                System.out.println("Invalid date format.");
            }
        }

        System.out.println("\nEnter Address Details");

        String city;
        while (true) {
            System.out.print("City: ");
            city = scanner.nextLine().trim();

            if (city.matches("[a-zA-Z ]+"))
                break;

            System.out.println("City should contain only alphabets.");
        }

        String state;
        while (true) {
            System.out.print("State: ");
            state = scanner.nextLine().trim();

            if (state.matches("[a-zA-Z ]+"))
                break;

            System.out.println("State should contain only alphabets.");
        }

        String zip;
        while (true) {
            System.out.print("Zip Code (6 digits): ");
            zip = scanner.nextLine().trim();

            if (zip.matches("[0-9]{6}"))
                break;

            System.out.println("Zip must be 6 digits.");
        }

        String country;
        while (true) {
            System.out.print("Country: ");
            country = scanner.nextLine().trim();

            if (country.matches("[a-zA-Z ]+"))
                break;

            System.out.println("Country should contain only alphabets.");
        }

        Address address = new Address(city, state, zip, country);

        int roleChoice = 1;

        if (admin) {
            while (true) {
                System.out.println("""
                        Select Role:
                        1. Client
                        2. Project Manager
                        3. Builder
                        """);

                String input = scanner.nextLine().trim();

                if (input.matches("[1-3]")) {
                    roleChoice = Integer.parseInt(input);
                    break;
                }

                System.out.println("Please select 1, 2 or 3.");
            }
        }

        UserRole role = switch (roleChoice) {
            case 1 -> UserRole.CLIENT;
            case 2 -> UserRole.PROJECT_MANAGER;
            case 3 -> UserRole.BUILDER;
            default -> UserRole.CLIENT;
        };

        User user = authService.register(name, phone, email, password, dob, address, role);

        if (user != null) {
            System.out.println("Registration successful. Welcome, " + user.getName() + "!");
            return user;
        }

        System.out.println("Registration failed. Email might already exist.");
        return null;
    }

    public static User authenticate(AuthenticationService authService, Scanner scanner) {

        while (true) {

            System.out.println("""
                    ==============================
                    1. Login
                    2. Register
                    3. Exit
                    ==============================
                    """);

            int choice = getValidChoice(scanner, "Choose an option");

            switch (choice) {
                case 1 -> {
                    User user = handleLogin(authService, scanner);
                    if (user != null) return user;
                }
                case 2 -> {
                    User user = handleRegister(authService, scanner, false);
                    if (user != null) return user;
                }
                case 3 -> {
                    System.out.println("Goodbye!");
                    return null;
                }
                default -> System.out.println("Please choose a valid option.");
            }
        }
    }
}
