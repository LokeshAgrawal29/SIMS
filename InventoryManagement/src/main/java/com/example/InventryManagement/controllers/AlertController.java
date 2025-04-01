package com.example.InventryManagement.controllers;

import com.example.InventryManagement.Entities.Alert;
import com.example.InventryManagement.Entities.AlertType;
import com.example.InventryManagement.Services.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@PreAuthorize("isAuthenticated()")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @GetMapping
    public ResponseEntity<?> getUnreadAlerts() {
        try {
            List<Alert> alerts = alertService.getUnreadAlerts();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", alerts
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getAlertsByType(@PathVariable AlertType type) {
        try {
            List<Alert> alerts = alertService.getAlertsByType(type);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", alerts
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAlertAsRead(@PathVariable Long id) {
        try {
            Alert alert = alertService.markAlertAsRead(id);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", alert
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/report/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getLowStockReport() {
        try {
            List<Alert> alerts = alertService.getAlertsByType(AlertType.LOW_STOCK);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", alerts
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/report/expiring")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getExpiringItemsReport() {
        try {
            List<Alert> alerts = alertService.getAlertsByType(AlertType.EXPIRY_WARNING);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", alerts
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTestAlert() {
        try {
            Alert alert = alertService.createTestAlert();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", alert
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllAlerts() {
        try {
            List<Alert> alerts = alertService.getAllAlerts();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", alerts
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
    }
} 