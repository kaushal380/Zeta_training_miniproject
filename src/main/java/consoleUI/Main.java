package consoleUI;

import models.Address;
import models.User;
import models.enums.ProjectType;
import models.enums.UserRole;
import repositories.UserRepository;
import services.AuthenticationService;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    static UserRepository userRepository = new UserRepository("users.json");
    static AuthenticationService authService = new AuthenticationService(userRepository);

    public static int getValidChoice(Scanner sc, String message){
        while (true) {
            try {
                System.out.print(message);
                int value = sc.nextInt();

                return value;

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                sc.nextLine();
            }
        }
    }

    private static User handleLogin(AuthenticationService authService, Scanner scanner) {

        System.out.println("Enter Email: ");
        String email = scanner.next();

        System.out.println("Enter Password: ");
        String password = scanner.next();

        User user = authService.login(email, password);

        if (user != null) {
            System.out.println("Login successful! Welcome " + user.getRole());
            return user;
        }

        System.out.println("Invalid credentials. Try again.");
        return null;
    }


    private static User handleRegister(AuthenticationService authService,
                                       Scanner scanner, boolean admin) {

        System.out.println("\n======================================");
        System.out.println("         USER REGISTRATION FORM       ");
        System.out.println("======================================");

        System.out.print("Enter Name        : ");
        String name = scanner.next();


        System.out.print("Enter Phone       : ");
        String phone = scanner.next();

        System.out.print("Enter Email       : ");
        String email = scanner.next();

        System.out.print("Enter Password    : ");
        String password = scanner.next();

        System.out.print("Enter DOB (YYYY-MM-DD) : ");
        LocalDate dob = LocalDate.parse(scanner.next());

        System.out.println("\n----------- Address Details -----------");

        System.out.print("Enter City        : ");
        String city = scanner.next();

        System.out.print("Enter State       : ");
        String state = scanner.next();

        int zip = getValidChoice(scanner, "Enter Zip Code    :");

        System.out.print("Enter Country     : ");
        String country = scanner.next();

        System.out.println("======================================\n");

        Address address = new Address(city, state, zip, country);


        int roleChoice;

        if (admin) {
            System.out.println("""
                    Select Role:
                    1. CLIENT
                    2. PROJECT_MANAGER
                    3. BUILDER
                    """);

            roleChoice = Integer.parseInt(scanner.nextLine());
        }
        else{
            roleChoice = 1;
        }

        UserRole role;

        switch (roleChoice) {
            case 1 -> role = UserRole.CLIENT;
            case 2 -> role = UserRole.PROJECT_MANAGER;
            case 3 -> role = UserRole.BUILDER;
            default -> throw new RuntimeException("Invalid role selection");
        }

        User user = authService.register(
                name,
                phone,
                email,
                password,
                dob,
                address,
                role
        );

        if (user != null) {
            System.out.println("Registration successful!");
            return user;
        } else {
            System.out.println("Registration failed.");
            return null;
        }
    }



    public static User authenticate(AuthenticationService authService, Scanner scanner) {

        while (true) {

            String message = """
                Authentication Menu:
                1. Login
                2. Register
                3. Logout
                Please make a choice: 
                """;

            int choice = getValidChoice(scanner, message);

            switch (choice) {

                case 1:
                    return handleLogin(authService, scanner);

                case 2:
                    return handleRegister(authService, scanner, false);

                case 3:
                    System.out.println("Logging out...");
                    return null;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }


    public static void main(String[] args) {


        System.out.println("Welcome to the builder portfolio app");

        User user = authenticate(authService, scanner);

        if (user == null){
            System.exit(0);
        }
        System.out.println(user);


    }
}
