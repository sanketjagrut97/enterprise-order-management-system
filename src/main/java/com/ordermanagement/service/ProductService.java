package com.ordermanagement.service;

import com.ordermanagement.exception.DuplicateSkuException;
import com.ordermanagement.dto.ProductRequest;
import com.ordermanagement.entity.Product;
import com.ordermanagement.exception.ProductNotFoundException;
import com.ordermanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product createProduct(ProductRequest request) {
        
        if (productRepository.existsBySku(request.getSku())) {
        throw new DuplicateSkuException(request.getSku());
        } 
        
        Product product = new Product();
        
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (productRepository.existsBySku(request.getSku())
            && !product.getSku().equals(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }
        
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productRepository.delete(product);
    }
}