package com.example.InventryManagement.Services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.InventryManagement.Entities.Product;
import com.example.InventryManagement.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Value("${app.inventory.low-stock-threshold:10}")
    private int lowStockThreshold;

    @Value("${app.inventory.expiry-alert-days:5}")
    private int expiryAlertDays;

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product updateProduct(Product product) {
        if (!productRepository.existsById(product.getId())) {
            throw new RuntimeException("Product not found");
        }
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getLowStockItems() {
        return productRepository.findLowStockItems(lowStockThreshold);
    }

    public List<Product> getExpiringItems() {
        LocalDate alertDate = LocalDate.now().plusDays(expiryAlertDays);
        return productRepository.findExpiringItems(alertDate);
    }

    public List<Product> getLowStockItemsByCategory(String category) {
        return productRepository.findLowStockItemsByCategory(category, lowStockThreshold);
    }
}
