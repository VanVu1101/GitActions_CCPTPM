package com.example.LeVanVu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderItemRequest(
        @NotNull(message = "productId là bắt buộc")
        Long productId,
        @NotNull(message = "quantity là bắt buộc")
        @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
        Integer quantity
) {
}

