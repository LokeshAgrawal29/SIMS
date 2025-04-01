package com.example.InventryManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.InventryManagement.Entities.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}