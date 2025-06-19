package view;

import java.util.Scanner;

public class CalculatorView {
    private Scanner scanner;

    public CalculatorView() {
        scanner = new Scanner(System.in);
    }

    public String getInput() {
        System.out.println("Enter a mathematical expression (or 'exit' to quit):");
        System.out.println("Supported operations: +, -, *, /, ^ (power), // (integer division)");
        System.out.print("> ");
        return scanner.nextLine().trim();
    }

    public void displayResult(double result) {
        System.out.println("Result: " + result);
        System.out.println();
    }

    public void displayError(String message) {
        System.err.println("Error: " + message);
        System.out.println();
    }

    public void close() {
        scanner.close();
    }
}
