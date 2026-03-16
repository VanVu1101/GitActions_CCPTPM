package com.example.LeVanVu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.LeVanVu.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

