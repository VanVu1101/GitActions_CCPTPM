package com.example.LeVanVu.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        @NotBlank(message = "Tên sản phẩm không được để trống")
        String name,
        String description,
        @NotNull(message = "Giá sản phẩm là bắt buộc")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá sản phẩm phải lớn hơn 0")
        BigDecimal price,
        @NotNull(message = "Tồn kho là bắt buộc")
        @Min(value = 0, message = "Tồn kho không được âm")
        Integer stock
) {
}

