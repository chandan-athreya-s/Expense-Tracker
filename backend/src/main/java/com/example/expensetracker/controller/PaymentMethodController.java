package com.example.expensetracker.controller;

import com.example.expensetracker.dto.PaymentMethodResponse;
import com.example.expensetracker.entity.PaymentMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController 
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {
    
    @GetMapping 
    public List<PaymentMethodResponse> getAll() {
        return Arrays.stream(PaymentMethod.values())
                .map(PaymentMethodResponse::from)
                .toList();
    }
}
