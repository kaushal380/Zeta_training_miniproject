package consoleUI.inputValidators;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputValidator {

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





}
