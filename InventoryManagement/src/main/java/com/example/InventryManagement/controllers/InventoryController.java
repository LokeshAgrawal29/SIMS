package com.example.InventryManagement.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.InventryManagement.Entities.Inventory;

import com.example.InventryManagement.Entities.StockMovement;
import com.example.InventryManagement.Entities.User;
import com.example.InventryManagement.Entities.MovementType;
import com.example.InventryManagement.Services.InventoryService;
import com.example.InventryManagement.Services.UserService;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private UserService userService;

    @PostMapping("/items")
    public ResponseEntity<?> addItem(@RequestBody Inventory item) {
        try {
            if (item.getName() == null || item.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\":\"Name is required\",\"status\":\"error\"}");
            }
            if (item.getQuantity() == null || item.getQuantity() < 0) {
                return ResponseEntity.badRequest().body("{\"error\":\"Valid quantity is required\",\"status\":\"error\"}");
            }
            if (item.getExpiryDate() == null) {
                return ResponseEntity.badRequest().body("{\"error\":\"Expiry date is required\",\"status\":\"error\"}");
            }
            if (item.getCategory() == null || item.getCategory().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\":\"Category is required\",\"status\":\"error\"}");
            }
            if (item.getSupplierName() == null || item.getSupplierName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\":\"Supplier name is required\",\"status\":\"error\"}");
            }
            if (item.getSupplierContact() == null || item.getSupplierContact().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\":\"Supplier contact is required\",\"status\":\"error\"}");
            }
            if (item.getSupplierEmail() == null || item.getSupplierEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\":\"Supplier email is required\",\"status\":\"error\"}");
            }
            if (item.getLowStockThreshold() == null || item.getLowStockThreshold() <= 0) {
                return ResponseEntity.badRequest().body("{\"error\":\"Valid low stock threshold required\",\"status\":\"error\"}");
            }
            if (item.getDescription() == null || item.getDescription().trim().isEmpty()) {
                item.setDescription("No description provided");
            }
            return ResponseEntity.ok(inventoryService.addInventory(item));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\",\"status\":\"error\"}");
        }
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<?> updateItem(
            @PathVariable Long id,
            @RequestBody Inventory updatedItem) {
        try {
            if (updatedItem.getName() == null || updatedItem.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\":\"Name is required\",\"status\":\"error\"}");
            }
            if (updatedItem.getQuantity() == null || updatedItem.getQuantity() < 0) {
                return ResponseEntity.badRequest().body("{\"error\":\"Valid quantity is required\",\"status\":\"error\"}");
            }
            if (updatedItem.getExpiryDate() == null) {
                return ResponseEntity.badRequest().body("{\"error\":\"Expiry date is required\",\"status\":\"error\"}");
            }
            if (updatedItem.getCategory() == null || updatedItem.getCategory().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\":\"Category is required\",\"status\":\"error\"}");
            }
            if (updatedItem.getSupplierName() == null || updatedItem.getSupplierName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\":\"Supplier name is required\",\"status\":\"error\"}");
            }
            if (updatedItem.getSupplierContact() == null || updatedItem.getSupplierContact().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\":\"Supplier contact is required\",\"status\":\"error\"}");
            }
            if (updatedItem.getSupplierEmail() == null || updatedItem.getSupplierEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\":\"Supplier email is required\",\"status\":\"error\"}");
            }
             // Existing validations
        if (updatedItem.getLowStockThreshold() == null || updatedItem.getLowStockThreshold() <= 0) {
            return ResponseEntity.badRequest().body("{\"error\":\"Valid low stock threshold required\",\"status\":\"error\"}");
        }
            updatedItem.setId(id);
            return ResponseEntity.ok(inventoryService.updateInventory(updatedItem));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\",\"status\":\"error\"}");
        }
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        try {
            inventoryService.deleteInventory(id);
            return ResponseEntity.ok().body("{\"message\":\"Item deleted successfully\",\"status\":\"success\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\",\"status\":\"error\"}");
        }
    }

    @GetMapping("/items")
    public ResponseEntity<?> getAllItems() {
        try {
            List<Inventory> items = inventoryService.getAllInventory();
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\",\"status\":\"error\"}");
        }
    }

    @GetMapping("/items/category/{category}")
    public ResponseEntity<?> getItemsByCategory(@PathVariable String category) {
        try {
            List<Inventory> items = inventoryService.getAllInventory().stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(category))
                .toList();
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\",\"status\":\"error\"}");
        }
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        try {
            Inventory item = inventoryService.getInventoryById(id);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\",\"status\":\"error\"}");
        }
    }

    @PostMapping("/items/{id}/stock")
    public ResponseEntity<?> updateStock(
            @PathVariable Long id,
            @RequestParam int quantityChange,
            @RequestParam MovementType movementType,
            @RequestParam String reason,
            Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        inventoryService.updateStock(id, quantityChange, movementType, reason, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items/{id}/movements")
    public ResponseEntity<List<StockMovement>> getStockMovements(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getStockMovements(id));
    }
}
