package com.example.expensetracker.config;

import com.example.expensetracker.entity.Category;
import com.example.expensetracker.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component 
public class CategorySeeder implements CommandLineRunner {
        private static final List<String> DEFAULT_CATEGORIES = List.of(
                "Rent",
                "Dining out",
                "Refreshments",
                "Personal Hygiene",
                "Household Supplies",
                "Groceries",
                "Transportation/Commuting",
                "Bills & Utilities",
                "Entertainment",
                "Healthcare",
                "Education",
                "Shopping",
                "Travel/Vacation",
                "Loan Repayment",
                "Savings",
                "Gifts & Donations",
                "Fuel",
                "Gym & Fitness",
                "Miscellaneous"
        );

        private final CategoryRepository categoryRepository;

        public CategorySeeder(CategoryRepository categoryRepository) {
            this.categoryRepository = categoryRepository;
        }

        @Override
        public void run(String... args) {
        for (String name : DEFAULT_CATEGORIES) {
            if (!categoryRepository.existsByName(name)) {
                categoryRepository.save(new Category(name));
                    }
                }
        }

}


    
