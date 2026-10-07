package com.example.expensetracker.dto;

import com.example.expensetracker.entity.PaymentMethod;

public record PaymentMethodResponse(String value, String label) {
    
    public static PaymentMethodResponse from(PaymentMethod method) {
        return new PaymentMethodResponse(method.name(), method.getLabel());
    }
}
