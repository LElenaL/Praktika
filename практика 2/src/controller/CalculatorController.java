package controller;

import model.CalculatorModel;
import view.CalculatorView;

public class CalculatorController {
    private CalculatorModel model;
    private CalculatorView view;

    public CalculatorController(CalculatorModel model, CalculatorView view) {
        this.model = model;
        this.view = view;
    }

    public void process() {
        while (true) {
            String input = view.getInput();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                double result = model.calculate(input);
                view.displayResult(result);
            } catch (Exception e) {
                view.displayError(e.getMessage());
            }
        }

        view.close();
    }
}
