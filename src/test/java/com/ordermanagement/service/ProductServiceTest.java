package com.ordermanagement.service;

import com.ordermanagement.dto.ProductRequest;
import com.ordermanagement.entity.Product;
import com.ordermanagement.exception.DuplicateSkuException;
import com.ordermanagement.exception.ProductNotFoundException;
import com.ordermanagement.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest productRequest;
    private Product product;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequest();
        productRequest.setSku("LAPTOP-001");
        productRequest.setName("Laptop");
        productRequest.setDescription("Business laptop");
        productRequest.setPrice(new BigDecimal("75000.00"));
        productRequest.setQuantity(10);

        product = new Product();
        product.setSku("LAPTOP-001");
        product.setName("Laptop");
        product.setDescription("Business laptop");
        product.setPrice(new BigDecimal("75000.00"));
        product.setQuantity(10);
    }

    @Test
    void createProductShouldSaveAndReturnProduct() {
        when(productRepository.existsBySku("LAPTOP-001"))
                .thenReturn(false);

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.createProduct(productRequest);

        assertNotNull(result);
        assertEquals("LAPTOP-001", result.getSku());
        assertEquals("Laptop", result.getName());
        assertEquals(new BigDecimal("75000.00"), result.getPrice());
        assertEquals(10, result.getQuantity());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProductShouldRejectDuplicateSku() {
        when(productRepository.existsBySku("LAPTOP-001"))
                .thenReturn(true);

        assertThrows(
                DuplicateSkuException.class,
                () -> productService.createProduct(productRequest)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getAllProductsShouldReturnProducts() {
        when(productRepository.findAll())
                .thenReturn(List.of(product));

        List<Product> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("LAPTOP-001", result.get(0).getSku());

        verify(productRepository).findAll();
    }

    @Test
    void getProductByIdShouldReturnExistingProduct() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("LAPTOP-001", result.getSku());

        verify(productRepository).findById(1L);
    }

    @Test
    void getProductByIdShouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(99L)
        );
    }

    @Test
    void updateProductShouldUpdateAndReturnProduct() {
        ProductRequest updatedRequest = new ProductRequest();
        updatedRequest.setSku("LAPTOP-001");
        updatedRequest.setName("Updated Laptop");
        updatedRequest.setDescription("Updated description");
        updatedRequest.setPrice(new BigDecimal("80000.00"));
        updatedRequest.setQuantity(15);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.updateProduct(1L, updatedRequest);

        assertEquals("Updated Laptop", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(new BigDecimal("80000.00"), result.getPrice());
        assertEquals(15, result.getQuantity());

        verify(productRepository).save(product);
    }

    @Test
    void updateProductShouldRejectDuplicateSku() {
        ProductRequest updatedRequest = new ProductRequest();
        updatedRequest.setSku("LAPTOP-002");
        updatedRequest.setName("Updated Laptop");
        updatedRequest.setPrice(new BigDecimal("80000.00"));
        updatedRequest.setQuantity(15);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.existsBySku("LAPTOP-002"))
                .thenReturn(true);

        assertThrows(
                DuplicateSkuException.class,
                () -> productService.updateProduct(1L, updatedRequest)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProductShouldDeleteExistingProduct() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> productService.deleteProduct(1L));

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProductShouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProduct(99L)
        );

        verify(productRepository, never()).delete(any(Product.class));
    }
}