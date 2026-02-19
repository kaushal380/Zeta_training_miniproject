package consoleUI.inputValidators;

import java.util.Scanner;

public class InputValidator {

    public static int getValidChoice(Scanner scanner, String message) {

        while (true) {
            System.out.print(message + ": ");
            String input = scanner.nextLine().trim();

            if (input.matches("\\d+")) {
                return Integer.parseInt(input);
            }

            System.out.println("Please enter a valid number.");
        }
    }
}
