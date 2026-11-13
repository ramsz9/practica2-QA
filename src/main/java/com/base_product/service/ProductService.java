package com.base_product.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.base_product.dto.ProductRequestDto;
import com.base_product.dto.ProductResponseDto;
import com.base_product.exception.DuplicateResourceException;
import com.base_product.exception.ResourceNotFoundException;
import com.base_product.model.ProductModel;
import com.base_product.repository.ProductRepository;

@Service
public class ProductService {

    private static final String PRODUCT_NOT_FOUND = "Product not found";
    private static final String SKU_ALREADY_EXISTS = "A product with this SKU already exists";

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponseDto> findAll() {
        return productRepository.findAll().stream().map(ProductModel::toDto).toList();
    }

    public ProductResponseDto findById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND))
                .toDto();
    }

    public ProductResponseDto create(ProductRequestDto dto) {
        if (productRepository.existsBySku(dto.sku())) {
            throw new DuplicateResourceException(SKU_ALREADY_EXISTS);
        }
        return productRepository.save(dto.toModel(null)).toDto();
    }

    public ProductResponseDto update(String id, ProductRequestDto dto) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(PRODUCT_NOT_FOUND);
        }
        if (productRepository.existsBySkuAndIdNot(dto.sku(), id)) {
            throw new DuplicateResourceException(SKU_ALREADY_EXISTS);
        }
        return productRepository.save(dto.toModel(id)).toDto();
    }

    public void deleteById(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(PRODUCT_NOT_FOUND);
        }
        productRepository.deleteById(id);
    }

}
