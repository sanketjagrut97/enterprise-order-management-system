package com.ordermanagement.service;
import com.ordermanagement.dto.ProductRequest;
import com.ordermanagement.entity.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service

public class ProductService {
    
    private final List<Product> products = new ArrayList<>();

    public ProductService(){
        products.add(
            new Product(
                1L,
                "KB-001",
                "Mechinical Keyboard",
                "Mechinical Keyboard with RGB lighting",
                new BigDecimal("2499.00"),
                10
            )
        );
    }

    public List<Product> getAllProducts(){
        return products;
    }

    public Product getProductById(Long id){
        return products.stream()
                 .filter(product -> product.getId().equals(id))
                 .findFirst()
                 .orElse(null);       
    }

    public Product createProduct(ProductRequest request) {

    Long nextId = products.stream()
            .mapToLong(Product::getId)
            .max()
            .orElse(0L) + 1;

    Product product = new Product(
            nextId,
            request.getSku(),
            request.getName(),
            request.getDescription(),
            request.getPrice(),
            request.getQuantity()
    );

    products.add(product);

    return product;
}
}
