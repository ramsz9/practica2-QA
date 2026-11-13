package com.base_product.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.base_product.model.ProductModel;

@Repository
public interface ProductRepository extends MongoRepository<ProductModel, String> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, String id);

}
