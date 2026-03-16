package com.example.LeVanVu.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CreateOrderRequest(
        @NotBlank(message = "Tên khách hàng không được để trống")
        String customerName,
        @NotBlank(message = "Số điện thoại không được để trống")
        String phone,
        @NotBlank(message = "Địa chỉ không được để trống")
        String address,
        @NotEmpty(message = "Đơn hàng phải có ít nhất 1 sản phẩm")
        List<@Valid CreateOrderItemRequest> items
) {
}

