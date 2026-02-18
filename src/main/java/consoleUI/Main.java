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



    public static void main(String[] args) {


        System.out.println("Welcome to the builder portfolio app");

        User user = AuthenticationDashboard.authenticate(authService, scanner);

        if (user == null){
            System.exit(0);
        }
        System.out.println(user);


    }
}
