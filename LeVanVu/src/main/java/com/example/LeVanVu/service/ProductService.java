package com.example.LeVanVu.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.LeVanVu.dto.ProductRequest;
import com.example.LeVanVu.dto.ProductResponse;
import com.example.LeVanVu.entity.Product;
import com.example.LeVanVu.exception.ResourceNotFoundException;
import com.example.LeVanVu.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.name().trim())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .build();
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findEntityById(id);
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = findEntityById(id);
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public Product findEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id = " + id));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
    }
}

