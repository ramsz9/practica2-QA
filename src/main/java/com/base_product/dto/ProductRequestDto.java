package com.base_product.dto;

import com.base_product.model.ProductModel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductRequestDto(
        @NotBlank(message = "name is required") String name,
        @NotBlank(message = "sku is required") String sku,
        @NotNull(message = "price is required") @Positive(message = "price must be greater than 0") Double price,
        @NotNull(message = "stock is required") @PositiveOrZero(message = "stock must be 0 or greater") Integer stock) {

    public ProductModel toModel(String id) {
        return new ProductModel(
                id,
                this.name,
                this.sku,
                this.price,
                this.stock);
    }

}
