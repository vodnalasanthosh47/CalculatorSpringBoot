package org.example.calculatorspringboot.Controller;

import org.example.calculatorspringboot.Model.CalculationResponse;
import org.example.calculatorspringboot.Service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/v1")
public class CalculatorController {
    @Autowired
    private CalculatorService service;

    @GetMapping("add")
    public CalculationResponse add(@RequestParam int a, @RequestParam int b) {
        return service.add(a, b);
    }

    @GetMapping("subtract")
    public CalculationResponse subtract(@RequestParam int a, @RequestParam int b) {
        return service.subtract(a, b);
    }

    @GetMapping("multiply")
    public CalculationResponse multiply(@RequestParam int a, @RequestParam int b) {
        return service.multiply(a, b);
    }

    @GetMapping("divide")
    public ResponseEntity<?> divide(@RequestParam int a, @RequestParam int b) {
        if (b == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Division by 0"));
        }

        return ResponseEntity.ok(service.divide(a, b));
    }
}
