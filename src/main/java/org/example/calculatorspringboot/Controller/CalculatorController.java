package org.example.calculatorspringboot.Controller;

import org.example.calculatorspringboot.Model.CalculationResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class CalculatorController {

    @GetMapping("add")
    public CalculationResponse add(@RequestParam int a, @RequestParam int b) {
        return new CalculationResponse("subtract", a, b, a+b);
    }

    @GetMapping("subtract")
    public CalculationResponse subtract(@RequestParam int a, @RequestParam int b) {
        return new CalculationResponse("subtract", a, b, a-b);
    }

    @GetMapping("multiply")
    public CalculationResponse multiply(@RequestParam int a, @RequestParam int b) {
        return new CalculationResponse("multiply", a, b, a*b);
    }
}
