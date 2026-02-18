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

        UserRepository userRepository = new UserRepository("users.json");

        AuthenticationService authService = new AuthenticationService(userRepository);

        Address address = new Address(
                "Bangalore",
                "Karnataka",
                560001,
                "India"
        );

        User user = authService.register(
                "tejas H S",
                "1234567890",
                "tejas@gmail.com",
                "password123",
                LocalDate.of(2005, 6, 7),
                address,
                UserRole.PROJECT_MANAGER
        );

        System.out.println("\n========== LOGIN ==========");

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
