package model;

import java.util.Stack;
import java.util.regex.Pattern;

public class CalculatorModel {
    public double calculate(String expression) throws IllegalArgumentException {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be empty");
        }

        // Упрощенная проверка начала и конца выражения
        if (!Pattern.matches("^[-+\\d(]?.*[\\d)!]$", expression)) {
            throw new IllegalArgumentException("Expression must start and end with valid terms");
        }

        // Проверка скобок
        if (!checkParentheses(expression)) {
            throw new IllegalArgumentException("Unbalanced parentheses");
        }

        // Преобразование в обратную польскую нотацию
        String rpn = convertToRPN(expression);

        // Вычисление выражения
        return evaluateRPN(rpn);
    }


    private boolean checkParentheses(String expression) {
        int balance = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) return false;
        }
        return balance == 0;
    }

    private String convertToRPN(String expression) {
        StringBuilder output = new StringBuilder();
        Stack<String> stack = new Stack<>();

        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);

            // Обработка чисел
            if (Character.isDigit(c) || c == '.') {
                StringBuilder num = new StringBuilder();
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    num.append(expression.charAt(i++));
                }
                output.append(num).append(" ");
                continue;
            }

            // Обработка унарного минуса
            if (c == '-' && (i == 0 || expression.charAt(i-1) == '(' ||
                    isOperator(expression.charAt(i-1)))) {
                output.append("-1 ");
                stack.push("*");
                i++;
                continue;
            }

            // Обработка функций
            if (i + 3 <= expression.length()) {
                String func = expression.substring(i, i+3);
                if (func.equals("exp") || func.equals("log")) {
                    stack.push(func);
                    i += 3;
                    continue;
                }
            }

            // Обработка операторов
            if (isOperator(c)) {
                String op = String.valueOf(c);
                // Особый случай для **
                if (c == '*' && i+1 < expression.length() && expression.charAt(i+1) == '*') {
                    op = "**";
                    i++;
                }

                while (!stack.isEmpty() &&
                        getPrecedence(stack.peek()) >= getPrecedence(op) &&
                        !stack.peek().equals("(")) {
                    output.append(stack.pop()).append(" ");
                }
                stack.push(op);
                i++;
                continue;
            }

            // Обработка скобок
            if (c == '(') {
                stack.push(String.valueOf(c));
                i++;
                continue;
            }

            if (c == ')') {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.append(stack.pop()).append(" ");
                }
                stack.pop(); // Удаляем "("
                i++;
                continue;
            }

            i++; // Пропускаем другие символы (пробелы и т.д.)
        }

        // Выталкиваем оставшиеся операторы
        while (!stack.isEmpty()) {
            output.append(stack.pop()).append(" ");
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
            }
            else if (isFunction(token)) {
                double a = stack.pop();
                stack.push(applyFunction(a, token));
            }
            else if (isOperator(token)) {
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
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '!';
    }

    private boolean isOperator(String op) {
        return op.equals("+") || op.equals("-") || op.equals("*") ||
                op.equals("/") || op.equals("^") || op.equals("**") || op.equals("!");
    }

    private boolean isFunction(String token) {
        return token.equals("exp") || token.equals("log");
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private int getPrecedence(String op) {
        switch (op) {
            case "!":
                return 5;
            case "^":
            case "**":
                return 4;
            case "exp":
            case "log":
                return 4;
            case "*":
            case "/":
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
            case "**":
                return Math.pow(a, b);
            case "!":
                return factorial(b);
            default:
                throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }

    private double applyFunction(double a, String func) {
        switch (func) {
            case "exp":
                return Math.exp(a);
            case "log":
                return Math.log(a) / Math.log(2);
            default:
                throw new IllegalArgumentException("Unknown function: " + func);
        }
    }

    private double factorial(double n) {
        if (n < 0) throw new IllegalArgumentException("Factorial of negative number");
        if (n == 0 || n == 1) return 1;
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
