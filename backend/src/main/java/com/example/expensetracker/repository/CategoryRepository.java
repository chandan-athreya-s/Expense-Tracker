package com.example.expensetracker.repository;

import com.example.expensetracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name); 
    List<Category> findByActiveTrueOrderByIdAsc();   
}
