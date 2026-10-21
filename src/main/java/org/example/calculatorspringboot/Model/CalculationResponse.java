package org.example.calculatorspringboot.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculationResponse {
    String operation;
    int a;
    int b;
    int result;
}
