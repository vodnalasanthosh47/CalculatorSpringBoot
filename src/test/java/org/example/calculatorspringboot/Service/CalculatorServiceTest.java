package org.example.calculatorspringboot.Service;

import org.example.calculatorspringboot.Model.CalculationResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTest {

    private final CalculatorService service = new CalculatorService();

    @Test
    void add() {
        assertEquals(new CalculationResponse("add", 56, 81, 137), service.add(56, 81));
    }

    @Test
    void subtract() {
        assertEquals(new CalculationResponse("subtract", 56, 27, 29), service.subtract(56, 27));
    }

    @Test
    void multiply() {
        assertEquals(new CalculationResponse("multiply", 3, 4, 12), service.multiply(3, 4));
    }

    @Test
    void divide() {
        assertEquals(new CalculationResponse("divide", 45, 5, 9), service.divide(45, 5));
    }
}