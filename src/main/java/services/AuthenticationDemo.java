package services;

import models.Address;
import models.User;
import models.enums.UserRole;
import repositories.UserRepository;

import java.time.LocalDate;
import java.util.Scanner;

public class AuthenticationDemo {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Create repository
        UserRepository userRepository = new UserRepository();

        // Create authentication service
        AuthenticationService authService = new AuthenticationService(userRepository);

        // Create address
        Address address = new Address(
                "Bangalore",
                "Karnataka",
                "560001",
                "India"
        );

        // Register user
        boolean isRegistered = authService.register(
                "Tejas H S",
                "9876543210",
                "tejas@gmail.com",
                "password123",
                LocalDate.of(2002, 4, 12),
                address,
                UserRole.ADMIN
        );

        System.out.println("\n========== LOGIN ==========");

        // --------- LOGIN INPUT ----------
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User loggedInUser = authService.login(email, password);

        if (loggedInUser != null) {
            System.out.println("\nWelcome " + loggedInUser.getRole());
            System.out.println(loggedInUser);
        } else {
            System.out.println("Login failed.");
        }

        scanner.close();

    }
}
