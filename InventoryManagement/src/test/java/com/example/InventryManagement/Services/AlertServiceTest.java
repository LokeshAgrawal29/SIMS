package com.example.InventryManagement.Services;

import com.example.InventryManagement.Entities.Alert;
import com.example.InventryManagement.Entities.AlertType;
import com.example.InventryManagement.Entities.InventoryItem;
import com.example.InventryManagement.repositories.AlertRepository;
import com.example.InventryManagement.repositories.InventoryItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @InjectMocks
    private AlertService alertService;

    private InventoryItem testItem;
    private Alert testAlert;
    private Alert expiryAlert;

    @BeforeEach
    void setUp() {
        testItem = new InventoryItem();
        testItem.setId(1L);
        testItem.setName("Test Item");
        testItem.setQuantity(5);
        testItem.setLowStockThreshold(10);
        testItem.setExpiryDate(LocalDateTime.now().plusDays(5));
        testItem.setCategory("Test Category");
        testItem.setSupplier("Test Supplier");

        testAlert = new Alert();
        testAlert.setId(1L);
        testAlert.setAlertType(AlertType.LOW_STOCK);
        testAlert.setMessage("Test Alert");
        testAlert.setInventoryItem(testItem);
        testAlert.setIsRead(false);

        expiryAlert = new Alert();
        expiryAlert.setId(2L);
        expiryAlert.setAlertType(AlertType.EXPIRY_WARNING);
        expiryAlert.setMessage("Expiry Test Alert");
        expiryAlert.setInventoryItem(testItem);
        expiryAlert.setIsRead(false);
    }

    @Test
    void createLowStockAlert_ShouldCreateAlert() {
        when(alertRepository.save(any(Alert.class))).thenReturn(testAlert);

        Alert result = alertService.createLowStockAlert(testItem);

        assertNotNull(result);
        assertEquals(AlertType.LOW_STOCK, result.getAlertType());
        assertEquals(testItem, result.getInventoryItem());
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void createExpiryAlert_ShouldCreateAlert() {
        when(alertRepository.save(any(Alert.class))).thenReturn(expiryAlert);

        Alert result = alertService.createExpiryAlert(testItem);

        assertNotNull(result);
        assertEquals(AlertType.EXPIRY_WARNING, result.getAlertType());
        assertEquals(testItem, result.getInventoryItem());
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void whenCheckExpiringItems_thenCreateAlertsForExpiringItems() {
        List<InventoryItem> expiringItems = Arrays.asList(testItem);
        when(inventoryItemRepository.findExpiringItems(any())).thenReturn(expiringItems);
        when(alertRepository.save(any(Alert.class))).thenReturn(expiryAlert);

        alertService.checkExpiringItems();

        verify(inventoryItemRepository).findExpiringItems(any());
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void getUnreadAlerts_ShouldReturnUnreadAlerts() {
        List<Alert> unreadAlerts = Arrays.asList(testAlert);
        when(alertRepository.findByIsReadFalse()).thenReturn(unreadAlerts);

        List<Alert> result = alertService.getUnreadAlerts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAlert, result.get(0));
        verify(alertRepository).findByIsReadFalse();
    }

    @Test
    void whenGetAlertsByType_thenReturnAlertsOfType() {
        List<Alert> lowStockAlerts = Arrays.asList(testAlert);
        when(alertRepository.findByAlertType(AlertType.LOW_STOCK)).thenReturn(lowStockAlerts);

        List<Alert> result = alertService.getAlertsByType(AlertType.LOW_STOCK);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(AlertType.LOW_STOCK, result.get(0).getAlertType());
        verify(alertRepository).findByAlertType(AlertType.LOW_STOCK);
    }

    @Test
    void markAlertAsRead_ShouldUpdateAlert() {
        when(alertRepository.findById(1L)).thenReturn(Optional.of(testAlert));
        when(alertRepository.save(any(Alert.class))).thenReturn(testAlert);

        Alert result = alertService.markAlertAsRead(1L);

        assertTrue(result.getIsRead());
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void whenMarkAlertAsRead_withInvalidId_thenThrowException() {
        when(alertRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> alertService.markAlertAsRead(999L));
        verify(alertRepository, never()).save(any(Alert.class));
    }
} 