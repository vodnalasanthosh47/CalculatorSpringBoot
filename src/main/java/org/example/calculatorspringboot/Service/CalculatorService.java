package org.example.calculatorspringboot.Service;

import org.example.calculatorspringboot.Model.CalculationResponse;
import org.springframework.stereotype.Service;

@Service
public class CalculatorService {
    public CalculationResponse add(int a, int b) {
        return new CalculationResponse("add", a, b, a+b);
    }

    public CalculationResponse subtract(int a, int b) {
        return new CalculationResponse("subtract", a, b, a-b);
    }

    public CalculationResponse multiply(int a, int b) {
        return new CalculationResponse("multiply", a, b, a*b);
    }

    public CalculationResponse divide(int a, int b) {
        return new CalculationResponse("divide", a, b, a/b);
    }
}
