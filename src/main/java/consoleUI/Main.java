package consoleUI;

import models.Address;
import models.User;
import models.enums.ProjectType;
import models.enums.UserRole;
import repositories.UserRepository;
import services.AuthenticationService;

import java.math.BigInteger;
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


    private static User handleRegister(AuthenticationService authService, Scanner scanner, boolean admin) {

        System.out.println("\n=========== USER REGISTRATION ===========");


        String name;
        while (true) {
            System.out.print("Enter Name        : ");
            name = scanner.next().trim();

            if (name.matches("[a-zA-Z ]+")) {
                break;
            } else {
                System.out.println("Invalid name. Only alphabets allowed.");
            }
        }

        String phone;
        while (true) {
            System.out.print("Enter Phone Number: ");
            phone = scanner.next().trim();

            if (phone.matches("[6-9][0-9]{9}")) {
                break;
            } else {
                System.out.println("Invalid phone. Must be 10 digits and start with 6-9.");
            }
        }

        String email;
        while (true) {
            System.out.print("Enter Email       : ");
            email = scanner.next().trim();

            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                break;
            } else {
                System.out.println("Invalid email format (example: abc@xyz.com)");
            }
        }

        String password;
        while (true) {
            System.out.print("Enter Password    : ");
            password = scanner.next().trim();

            if (!password.isEmpty()) {
                break;
            } else {
                System.out.println("Password cannot be empty.");
            }
        }

        LocalDate dob;

        while (true) {
            try {
                System.out.print("Enter DOB (YYYY-MM-DD): ");
                dob = LocalDate.parse(scanner.next().trim());

                if (!dob.isBefore(LocalDate.now())) {
                    System.out.println("Date of birth must be in the past.");
                    continue;
                }

                break;

            } catch (Exception e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }


        System.out.println("\n----------- Address Details -----------");


        String city;
        while (true) {
            System.out.print("Enter city        : ");
            city = scanner.next().trim();

            if (city.matches("[a-zA-Z ]+")) {
                break;
            } else {
                System.out.println("Invalid name. Only alphabets allowed.");
            }
        }


        String state;
        while (true) {
            System.out.print("Enter state        : ");
            state = scanner.next().trim();

            if (state.matches("[a-zA-Z ]+")) {
                break;
            } else {
                System.out.println("Invalid name. Only alphabets allowed.");
            }
        }


        String zip;
        while (true) {
            System.out.print("Enter Zip Code    : ");
            zip = scanner.next().trim();

            if (zip.matches("[0-9]{6}")) {  // Indian 6-digit PIN
                break;
            } else {
                System.out.println("Invalid zip. Must be 6 digits.");
            }
        }

        String country;
        while (true) {
            System.out.print("Enter country        : ");
            country = scanner.next().trim();

            if (country.matches("[a-zA-Z ]+")) {
                break;
            } else {
                System.out.println("Invalid name. Only alphabets allowed.");
            }
        }


        Address address = new Address(city, state, zip, country);

        int roleChoice;

        if (admin) {
            while (true) {
                System.out.println("""
                    Select Role:
                    1. CLIENT
                    2. PROJECT_MANAGER
                    3. BUILDER
                    """);

                String input = scanner.nextLine();

                if (input.matches("[1-3]")) {
                    roleChoice = Integer.parseInt(input);
                    break;
                } else {
                    System.out.println("Invalid role selection. Choose 1-3.");
                }
            }
        } else {
            roleChoice = 1; // Default CLIENT
        }

        UserRole role = switch (roleChoice) {
            case 1 -> UserRole.CLIENT;
            case 2 -> UserRole.PROJECT_MANAGER;
            case 3 -> UserRole.BUILDER;
            default -> throw new RuntimeException("Invalid role selection");
        };

        User user = authService.register(
                name,
                phone, // keep as String unless you truly need BigInteger
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
