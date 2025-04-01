package com.example.InventryManagement.repositories;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.InventryManagement.Entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM Product p WHERE p.quantity <= :threshold")
    List<Product> findLowStockItems(@Param("threshold") int threshold);
    
    @Query("SELECT p FROM Product p WHERE p.expiryDate <= :date")
    List<Product> findExpiringItems(@Param("date") LocalDate date);
    
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.quantity <= :threshold")
    List<Product> findLowStockItemsByCategory(@Param("category") String category, @Param("threshold") int threshold);
}