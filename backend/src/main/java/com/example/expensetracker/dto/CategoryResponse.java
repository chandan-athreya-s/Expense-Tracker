package com.example.expensetracker.dto;

import com.example.expensetracker.entity.Category;

public record CategoryResponse(Long id, String name) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }    
}
