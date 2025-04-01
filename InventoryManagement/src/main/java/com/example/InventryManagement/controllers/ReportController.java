package com.example.InventryManagement.controllers;

import com.example.InventryManagement.Entities.StockMovement;
import com.example.InventryManagement.Services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    @GetMapping("/daily")
    public ResponseEntity<List<StockMovement>> getDailyReport() {
        return ResponseEntity.ok(reportService.generateDailyReport());
    }
    
    @GetMapping("/weekly")
    public ResponseEntity<List<StockMovement>> getWeeklyReport() {
        return ResponseEntity.ok(reportService.generateWeeklyReport());
    }
    
    @GetMapping("/category-summary")
    public ResponseEntity<Map<String, Long>> getCategorySummary() {
        return ResponseEntity.ok(reportService.generateCategorySummary());
    }
    
    @GetMapping("/stock-level-summary")
    public ResponseEntity<Map<String, Integer>> getStockLevelSummary() {
        return ResponseEntity.ok(reportService.generateStockLevelSummary());
    }
} 