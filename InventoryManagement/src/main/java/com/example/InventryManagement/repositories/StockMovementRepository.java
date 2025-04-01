package com.example.InventryManagement.repositories;

import com.example.InventryManagement.Entities.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByInventoryItemId(Long inventoryItemId);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementDate BETWEEN :startDate AND :endDate")
    List<StockMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementDate >= :startDate")
    List<StockMovement> findMovementsSince(LocalDateTime startDate);
} 