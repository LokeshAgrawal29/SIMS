package com.example.InventryManagement.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.InventryManagement.Entities.Inventory;
import com.example.InventryManagement.repositories.InventoryRepository;
import com.example.InventryManagement.Entities.InventoryItem;
import com.example.InventryManagement.Entities.StockMovement;
import com.example.InventryManagement.Entities.User;
import com.example.InventryManagement.Entities.MovementType;
import com.example.InventryManagement.repositories.InventoryItemRepository;
import com.example.InventryManagement.repositories.StockMovementRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    
    @Autowired
    private StockMovementRepository stockMovementRepository;
    
    @Autowired
    private AlertService alertService;

    public Inventory addInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
    }

    public Inventory updateInventory(Inventory inventory) {
        if (!inventoryRepository.existsById(inventory.getId())) {
            throw new RuntimeException("Inventory not found with id: " + inventory.getId());
        }
        return inventoryRepository.save(inventory);
    }

    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new RuntimeException("Inventory not found with id: " + id);
        }
        inventoryRepository.deleteById(id);
    }

    @Transactional
    public InventoryItem addItem(InventoryItem item) {
        InventoryItem savedItem = inventoryItemRepository.save(item);
        createStockMovement(savedItem, item.getQuantity(), MovementType.ADD, "Initial stock", null);
        return savedItem;
    }
    
    @Transactional
    public InventoryItem updateItem(Long id, InventoryItem updatedItem) {
        InventoryItem existingItem = inventoryItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        // Update fields
        existingItem.setName(updatedItem.getName());
        existingItem.setCategory(updatedItem.getCategory());
        existingItem.setSupplier(updatedItem.getSupplier());
        existingItem.setLowStockThreshold(updatedItem.getLowStockThreshold());
        existingItem.setDescription(updatedItem.getDescription());
        
        return inventoryItemRepository.save(existingItem);
    }
    
    @Transactional
    public void deleteItem(Long id) {
        if (!inventoryItemRepository.existsById(id)) {
            throw new RuntimeException("Item not found");
        }
        inventoryItemRepository.deleteById(id);
    }
    
    @Transactional
    public void updateStock(Long itemId, int quantityChange, MovementType movementType, String reason, User user) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        int newQuantity = item.getQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        
        item.setQuantity(newQuantity);
        inventoryItemRepository.save(item);
        
        createStockMovement(item, quantityChange, movementType, reason, user);
        
        // Check for low stock alert
        if (newQuantity <= item.getLowStockThreshold()) {
            alertService.createLowStockAlert(item);
        }
    }
    
    private void createStockMovement(InventoryItem item, int quantityChange, MovementType type, String reason, User user) {
        StockMovement movement = new StockMovement();
        movement.setInventoryItem(item);
        movement.setQuantityChange(quantityChange);
        movement.setMovementType(type);
        movement.setReason(reason);
        movement.setUser(user);
        stockMovementRepository.save(movement);
    }
    
    public List<InventoryItem> getAllItems() {
        return inventoryItemRepository.findAll();
    }
    
    public List<InventoryItem> getItemsByCategory(String category) {
        return inventoryItemRepository.findByCategory(category);
    }
    
    public List<StockMovement> getStockMovements(Long itemId) {
        return stockMovementRepository.findByInventoryItemId(itemId);
    }
    
    public List<StockMovement> getMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return stockMovementRepository.findByDateRange(startDate, endDate);
    }
}