package com.example.InventryManagement.repositories;

import com.example.InventryManagement.Entities.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByCategory(String category);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= i.lowStockThreshold")
    List<InventoryItem> findLowStockItems();
    
    @Query("SELECT i FROM InventoryItem i WHERE i.expiryDate <= :threshold")
    List<InventoryItem> findExpiringItems(LocalDateTime threshold);
} 