package com.example.InventryManagement.Services;

import com.example.InventryManagement.Entities.Alert;
import com.example.InventryManagement.Entities.AlertType;
import com.example.InventryManagement.Entities.InventoryItem;
import com.example.InventryManagement.repositories.AlertRepository;
import com.example.InventryManagement.repositories.InventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    
    public Alert createLowStockAlert(InventoryItem item) {
        Alert alert = new Alert();
        alert.setInventoryItem(item);
        alert.setAlertType(AlertType.LOW_STOCK);
        alert.setMessage(String.format("Low stock alert: %s has only %d items remaining", 
            item.getName(), item.getQuantity()));
        return alertRepository.save(alert);
    }
    
    public Alert createExpiryAlert(InventoryItem item) {
        Alert alert = new Alert();
        alert.setInventoryItem(item);
        alert.setAlertType(AlertType.EXPIRY_WARNING);
        alert.setMessage(String.format("Expiry warning: %s will expire on %s", 
            item.getName(), item.getExpiryDate()));
        return alertRepository.save(alert);
    }
    
    @Scheduled(cron = "0 0 9 * * *") // Run at 9 AM every day
    @Transactional
    public void checkExpiringItems() {
        LocalDateTime threshold = LocalDateTime.now().plusDays(7); // Items expiring in next 7 days
        List<InventoryItem> expiringItems = inventoryItemRepository.findExpiringItems(threshold);
        
        for (InventoryItem item : expiringItems) {
            createExpiryAlert(item);
        }
    }
    
    @Transactional
    public Alert createTestAlert() {
        System.out.println("Starting test alert creation...");
        try {
            // Create a test inventory item
            InventoryItem testItem = new InventoryItem();
            testItem.setName("Test Item");
            testItem.setDescription("This is a test item");
            testItem.setQuantity(5);
            testItem.setCategory("Test Category");
            testItem.setSupplier("Test Supplier");
            testItem.setLowStockThreshold(10);
            testItem.setExpiryDate(LocalDateTime.now().plusDays(5));
            testItem = inventoryItemRepository.save(testItem);
            System.out.println("Created test inventory item with ID: " + testItem.getId());

            // Create the alert
            Alert alert = new Alert();
            alert.setInventoryItem(testItem);
            alert.setAlertType(AlertType.LOW_STOCK);
            alert.setMessage("This is a test alert for " + testItem.getName());
            alert.setIsRead(false);
            alert = alertRepository.save(alert);
            System.out.println("Created test alert with ID: " + alert.getId());
            System.out.println("Alert details: " + alert.toString());

            // Verify alert was saved
            Alert savedAlert = alertRepository.findById(alert.getId()).orElse(null);
            if (savedAlert != null) {
                System.out.println("Verified alert exists in database with ID: " + savedAlert.getId());
                System.out.println("Alert read status: " + savedAlert.getIsRead());
            } else {
                System.out.println("WARNING: Alert not found in database after saving!");
            }

            return alert;
        } catch (Exception e) {
            System.out.println("Error creating test alert: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<Alert> getUnreadAlerts() {
        System.out.println("Starting to fetch unread alerts...");
        try {
            // First, get all alerts to verify database state
            List<Alert> allAlerts = alertRepository.findAll();
            System.out.println("Total alerts in database: " + allAlerts.size());
            allAlerts.forEach(a -> System.out.println("Alert ID: " + a.getId() + 
                ", Read: " + a.getIsRead() + 
                ", Type: " + a.getAlertType()));

            // Now get unread alerts
            List<Alert> unreadAlerts = alertRepository.findByIsReadFalse();
            System.out.println("Found " + unreadAlerts.size() + " unread alerts");
            
            if (unreadAlerts.isEmpty()) {
                System.out.println("No unread alerts found. Checking if any alerts exist...");
                long totalAlerts = alertRepository.count();
                System.out.println("Total alerts in database: " + totalAlerts);
            } else {
                unreadAlerts.forEach(alert -> System.out.println("Unread Alert ID: " + alert.getId() + 
                    ", Type: " + alert.getAlertType() + 
                    ", Message: " + alert.getMessage()));
            }
            return unreadAlerts;
        } catch (Exception e) {
            System.out.println("Error fetching unread alerts: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<Alert> getAlertsByType(AlertType type) {
        System.out.println("Fetching alerts of type: " + type);
        List<Alert> alerts = alertRepository.findByAlertType(type);
        System.out.println("Found " + alerts.size() + " alerts of type " + type);
        return alerts;
    }
    
    @Transactional
    public Alert markAlertAsRead(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setIsRead(true);
        return alertRepository.save(alert);
    }
    
    public List<Alert> getAllAlerts() {
        System.out.println("Fetching all alerts...");
        try {
            List<Alert> alerts = alertRepository.findAllAlerts();
            System.out.println("Found " + alerts.size() + " total alerts");
            alerts.forEach(alert -> System.out.println("Alert ID: " + alert.getId() + 
                ", Type: " + alert.getAlertType() + 
                ", Read: " + alert.getIsRead() + 
                ", Message: " + alert.getMessage()));
            return alerts;
        } catch (Exception e) {
            System.out.println("Error fetching all alerts: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 