package com.example.LeVanVu.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.LeVanVu.entity.Product;
import com.example.LeVanVu.repository.ProductRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedProducts(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() > 0) {
                return;
            }

            productRepository.saveAll(List.of(
                    Product.builder()
                            .name("Áo thun basic")
                            .description("Áo thun cotton mặc hằng ngày")
                            .price(new BigDecimal("149000"))
                            .stock(40)
                            .build(),
                    Product.builder()
                            .name("Quần jean xanh")
                            .description("Quần jean form regular dễ phối đồ")
                            .price(new BigDecimal("399000"))
                            .stock(25)
                            .build(),
                    Product.builder()
                            .name("Giày sneaker trắng")
                            .description("Sneaker phong cách tối giản")
                            .price(new BigDecimal("699000"))
                            .stock(15)
                            .build()
            ));
        };
    }
}

