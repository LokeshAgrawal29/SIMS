package com.example.InventryManagement.Services;

import com.example.InventryManagement.Entities.StockMovement;
import com.example.InventryManagement.repositories.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {
    
    @Autowired
    private StockMovementRepository stockMovementRepository;
    
    public List<StockMovement> generateDailyReport() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return stockMovementRepository.findByDateRange(startOfDay, endOfDay);
    }
    
    public List<StockMovement> generateWeeklyReport() {
        LocalDateTime startOfWeek = LocalDateTime.now().minusWeeks(1);
        return stockMovementRepository.findMovementsSince(startOfWeek);
    }
    
    public Map<String, Long> generateCategorySummary() {
        return stockMovementRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                movement -> movement.getInventoryItem().getCategory(),
                Collectors.counting()
            ));
    }
    
    public Map<String, Integer> generateStockLevelSummary() {
        return stockMovementRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                movement -> movement.getInventoryItem().getName(),
                Collectors.summingInt(movement -> movement.getQuantityChange())
            ));
    }
} 