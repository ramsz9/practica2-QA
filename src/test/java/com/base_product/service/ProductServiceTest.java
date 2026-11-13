package com.base_product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.base_product.dto.ProductRequestDto;
import com.base_product.dto.ProductResponseDto;
import com.base_product.exception.DuplicateResourceException;
import com.base_product.exception.ResourceNotFoundException;
import com.base_product.model.ProductModel;
import com.base_product.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Should return all products")
    void testFindAll() {
        ProductModel product1 = new ProductModel("p1", "Keyboard", "KEY-001", 499.9, 10);
        ProductModel product2 = new ProductModel("p2", "Mouse", "MOU-001", 199.9, 25);

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        List<ProductResponseDto> result = productService.findAll();

        assertEquals(2, result.size());
        assertEquals("Keyboard", result.get(0).name());
        assertEquals("Mouse", result.get(1).name());
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should return DTO when product ID exists")
    void testFindById() {
        ProductModel product = new ProductModel("p1", "Keyboard", "KEY-001", 499.9, 10);

        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        ProductResponseDto result = productService.findById("p1");

        assertEquals(product.toDto(), result);
        verify(productRepository).findById("p1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product ID is not found")
    void testFindByIdNotFound() {
        when(productRepository.findById("p99")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productService.findById("p99"));

        assertEquals("Product not found", ex.getMessage());
    }

    @Test
    @DisplayName("Should save the request data and return the created product")
    void testCreate() {
        ProductRequestDto request = new ProductRequestDto("Keyboard", "KEY-001", 499.9, 10);
        ProductModel saved = new ProductModel("p1", "Keyboard", "KEY-001", 499.9, 10);

        when(productRepository.existsBySku("KEY-001")).thenReturn(false);
        when(productRepository.save(any(ProductModel.class))).thenReturn(saved);

        ProductResponseDto result = productService.create(request);

        ArgumentCaptor<ProductModel> captor = ArgumentCaptor.forClass(ProductModel.class);
        verify(productRepository).save(captor.capture());
        ProductModel sent = captor.getValue();
        assertNull(sent.getId());
        assertEquals("Keyboard", sent.getName());
        assertEquals("KEY-001", sent.getSku());
        assertEquals(499.9, sent.getPrice());
        assertEquals(10, sent.getStock());
        assertNotNull(result);
        assertEquals("p1", result.id());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when SKU already exists on create")
    void testCreateDuplicateSku() {
        ProductRequestDto request = new ProductRequestDto("Keyboard", "KEY-001", 499.9, 10);

        when(productRepository.existsBySku("KEY-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> productService.create(request));
        verify(productRepository, never()).save(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should update the product keeping its ID")
    void testUpdate() {
        ProductRequestDto request = new ProductRequestDto("Keyboard RGB", "KEY-001", 599.9, 8);
        ProductModel updated = new ProductModel("p1", "Keyboard RGB", "KEY-001", 599.9, 8);

        when(productRepository.existsById("p1")).thenReturn(true);
        when(productRepository.existsBySkuAndIdNot("KEY-001", "p1")).thenReturn(false);
        when(productRepository.save(any(ProductModel.class))).thenReturn(updated);

        ProductResponseDto result = productService.update("p1", request);

        ArgumentCaptor<ProductModel> captor = ArgumentCaptor.forClass(ProductModel.class);
        verify(productRepository).save(captor.capture());
        assertEquals("p1", captor.getValue().getId());
        assertEquals("Keyboard RGB", captor.getValue().getName());
        assertEquals(updated.toDto(), result);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating a missing product")
    void testUpdateNotFound() {
        ProductRequestDto request = new ProductRequestDto("Keyboard", "KEY-001", 499.9, 10);

        when(productRepository.existsById("p99")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.update("p99", request));
        verify(productRepository, never()).save(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when SKU belongs to another product")
    void testUpdateDuplicateSku() {
        ProductRequestDto request = new ProductRequestDto("Keyboard", "MOU-001", 499.9, 10);

        when(productRepository.existsById("p1")).thenReturn(true);
        when(productRepository.existsBySkuAndIdNot("MOU-001", "p1")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> productService.update("p1", request));
        verify(productRepository, never()).save(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should delete product by ID")
    void testDeleteById() {
        when(productRepository.existsById("p1")).thenReturn(true);

        productService.deleteById("p1");

        verify(productRepository).deleteById("p1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting a missing product")
    void testDeleteByIdNotFound() {
        when(productRepository.existsById("p99")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteById("p99"));
        verify(productRepository, never()).deleteById("p99");
    }

}
