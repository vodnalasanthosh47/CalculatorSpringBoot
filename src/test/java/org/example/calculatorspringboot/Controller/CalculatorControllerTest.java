package org.example.calculatorspringboot.Controller;

import org.example.calculatorspringboot.Service.CalculatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculatorController.class)
@Import(CalculatorService.class)
class CalculatorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAdd() throws Exception {
        mockMvc.perform(get("/api/v1/add")
                            .param("a", "13")
                            .param("b", "58"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").exists())
                .andExpect(jsonPath("$.operation").value("add"))
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result").value(71));
    }

    @Test
    void testSubtract() throws Exception {
        mockMvc.perform(get("/api/v1/subtract")
                        .param("a", "643")
                        .param("b", "29"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").exists())
                .andExpect(jsonPath("$.operation").value("subtract"))
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result").value(614));
    }

    @Test
    void testMultiply() throws Exception {
        mockMvc.perform(get("/api/v1/multiply")
                        .param("a", "63")
                        .param("b", "11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").exists())
                .andExpect(jsonPath("$.operation").value("multiply"))
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result").value(693));
    }

    @Test
    void testDivisionNotByZero() throws Exception {
        mockMvc.perform(get("/api/v1/divide")
                        .param("a", "25")
                        .param("b", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").exists())
                .andExpect(jsonPath("$.operation").value("divide"))
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result").value(5));
    }

    @Test
    void testDivisionByZero() throws Exception {
        mockMvc.perform(get("/api/v1/divide")
                        .param("a", "25")
                        .param("b", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value("Division by 0"));
    }
}