package com.example.LeVanVu.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.LeVanVu.dto.CreateOrderItemRequest;
import com.example.LeVanVu.dto.CreateOrderRequest;
import com.example.LeVanVu.dto.OrderItemResponse;
import com.example.LeVanVu.dto.OrderResponse;
import com.example.LeVanVu.entity.CustomerOrder;
import com.example.LeVanVu.entity.OrderItem;
import com.example.LeVanVu.entity.Product;
import com.example.LeVanVu.exception.BadRequestException;
import com.example.LeVanVu.exception.ResourceNotFoundException;
import com.example.LeVanVu.repository.CustomerOrderRepository;
import com.example.LeVanVu.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return customerOrderRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        CustomerOrder order = customerOrderRepository.findWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với id = " + id));
        return toResponse(order);
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        Map<Long, Integer> mergedItems = mergeItems(request.items());
        CustomerOrder order = CustomerOrder.builder()
                .customerName(request.customerName().trim())
                .phone(request.phone().trim())
                .address(request.address().trim())
                .createdAt(LocalDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : mergedItems.entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id = " + entry.getKey()));

            int quantity = entry.getValue();
            if (product.getStock() < quantity) {
                throw new BadRequestException("Sản phẩm '" + product.getName() + "' không đủ tồn kho");
            }

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            product.setStock(product.getStock() - quantity);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .lineTotal(lineTotal)
                    .build();

            order.getItems().add(item);
            totalAmount = totalAmount.add(lineTotal);
        }

        order.setTotalAmount(totalAmount);
        CustomerOrder savedOrder = customerOrderRepository.save(order);
        return toResponse(savedOrder);
    }

    private Map<Long, Integer> mergeItems(List<CreateOrderItemRequest> items) {
        Map<Long, Integer> mergedItems = new LinkedHashMap<>();
        for (CreateOrderItemRequest item : items) {
            mergedItems.merge(item.productId(), item.quantity(), Integer::sum);
        }
        return mergedItems;
    }

    private OrderResponse toResponse(CustomerOrder order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getPhone(),
                order.getAddress(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                itemResponses);
    }
}

