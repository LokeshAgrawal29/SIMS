package com.example.InventryManagement.repositories;

import com.example.InventryManagement.Entities.Alert;
import com.example.InventryManagement.Entities.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByIsReadFalse();
    List<Alert> findByAlertType(AlertType alertType);
    List<Alert> findByInventoryItemId(Long inventoryItemId);
    
    @Query("SELECT a FROM Alert a")
    List<Alert> findAllAlerts();
} 