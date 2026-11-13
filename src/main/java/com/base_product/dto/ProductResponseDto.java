package com.base_product.dto;

public record ProductResponseDto(
        String id,
        String name,
        String sku,
        Double price,
        Integer stock) {

}
