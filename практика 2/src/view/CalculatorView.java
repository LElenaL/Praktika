package view;

import java.util.Scanner;

public class CalculatorView {
    private Scanner scanner;

    public CalculatorView() {
        scanner = new Scanner(System.in);
    }

    public String getInput() {
        System.out.println("Enter a mathematical expression (or 'exit' to quit):");
        System.out.println("Supported operations: +, -, *, /, ^ or ** (power), ! (factorial)");
        System.out.println("Supported functions: exp(), log() (base 2)");
        System.out.println("Example: -3234+((exp(2)*843/log(3234)-4232123)/(34+123+32+5))*3234");
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
