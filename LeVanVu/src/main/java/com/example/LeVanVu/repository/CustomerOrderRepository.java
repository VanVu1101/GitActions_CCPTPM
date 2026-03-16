package com.example.LeVanVu.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.LeVanVu.entity.CustomerOrder;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    @Override
    @EntityGraph(attributePaths = {"items", "items.product"})
    java.util.List<CustomerOrder> findAll();

    @EntityGraph(attributePaths = {"items", "items.product"})
    java.util.Optional<CustomerOrder> findWithItemsById(Long id);
}

