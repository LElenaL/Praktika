package model;

import java.util.Stack;
import java.util.regex.Pattern;

public class CalculatorModel {
    public double calculate(String expression) throws IllegalArgumentException {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be empty");
        }

        if (!Pattern.matches("^-?\\d.*\\d$", expression)) {
            throw new IllegalArgumentException("Expression must start and end with a number");
        }

        String[] tokens = expression.split("(?<=[-+*/^//])|(?=[-+*/^//])");
        if (tokens.length > 199) {
            throw new IllegalArgumentException("Too many terms (maximum 100)");
        }

        String rpn = convertToRPN(expression);
        return evaluateRPN(rpn);
    }

    private String convertToRPN(String expression) {
        StringBuilder output = new StringBuilder();
        Stack<String> operatorStack = new Stack<>();

        int i = 0;
        if (expression.charAt(0) == '-') {
            output.append('-');
            i = 1;
        }

        while (i < expression.length()) {
            char c = expression.charAt(i);

            // Проверка на целочисленное деление "//"
            if (i < expression.length() - 1 && c == '/' && expression.charAt(i + 1) == '/') {
                while (!operatorStack.isEmpty() &&
                        getPrecedence(operatorStack.peek()) >= getPrecedence("//")) {
                    output.append(operatorStack.pop()).append(' ');
                }
                operatorStack.push("//");
                i += 2;
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    output.append(expression.charAt(i++));
                }
                output.append(' ');
                continue;
            }

            if (isOperator(c) || c == '(' || c == ')') {
                if (c == '(') {
                    operatorStack.push(String.valueOf(c));
                } else if (c == ')') {
                    while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                        output.append(operatorStack.pop()).append(' ');
                    }
                    operatorStack.pop();
                } else {
                    String op = String.valueOf(c);
                    while (!operatorStack.isEmpty() &&
                            getPrecedence(operatorStack.peek()) >= getPrecedence(op)) {
                        output.append(operatorStack.pop()).append(' ');
                    }
                    operatorStack.push(op);
                }
                i++;
            } else {
                i++;
            }
        }

        while (!operatorStack.isEmpty()) {
            output.append(operatorStack.pop()).append(' ');
        }

        return output.toString();
    }

    private double evaluateRPN(String rpn) {
        Stack<Double> stack = new Stack<>();
        String[] tokens = rpn.split("\\s+");

        for (String token : tokens) {
            if (token.isEmpty()) continue;

            if (isNumeric(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid expression");
                }
                double b = stack.pop();
                double a = stack.pop();
                stack.push(applyOperator(a, b, token));
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid expression");
        }

        return stack.pop();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    private boolean isOperator(String op) {
        return op.equals("+") || op.equals("-") || op.equals("*") ||
                op.equals("/") || op.equals("^") || op.equals("//");
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int getPrecedence(String op) {
        switch (op) {
            case "^":
                return 4;
            case "*":
            case "/":
            case "//":
                return 3;
            case "+":
            case "-":
                return 2;
            default:
                return 0;
        }
    }

    private double applyOperator(double a, double b, String op) {
        switch (op) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
            case "^":
                return Math.pow(a, b);
            case "//":
                if (b == 0) throw new ArithmeticException("Division by zero");
                return (int)(a / b);
            default:
                throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }
}
