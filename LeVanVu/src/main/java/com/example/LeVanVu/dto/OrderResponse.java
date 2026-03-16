package com.example.LeVanVu.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerName,
        String phone,
        String address,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
}

