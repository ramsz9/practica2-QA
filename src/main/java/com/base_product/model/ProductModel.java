package com.base_product.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.base_product.dto.ProductResponseDto;

@Document(collection = "products")
public class ProductModel {

    @Id
    private String id;
    private String name;
    private String sku;
    private Double price;
    private Integer stock;

    public ProductModel() {
    }

    public ProductModel(String id, String name, String sku, Double price, Integer stock) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public ProductResponseDto toDto() {
        return new ProductResponseDto(
                this.id,
                this.name,
                this.sku,
                this.price,
                this.stock);
    }

}
