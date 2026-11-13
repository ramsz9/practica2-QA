package com.base_product.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.base_product.model.ProductModel;
import com.base_product.repository.ProductRepository;

// Integration tests: real Spring context + real MongoDB (separate products_test database)
@SpringBootTest(properties = "spring.mongodb.uri=mongodb://localhost:27017/products_test")
@AutoConfigureMockMvc
class ProductControllerTest {

    private static final String KEYBOARD_JSON = """
            {"name": "Keyboard", "sku": "KEY-001", "price": 499.9, "stock": 10}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /product creates the product and returns 201")
    void createProduct() throws Exception {
        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(KEYBOARD_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.sku").value("KEY-001"))
                .andExpect(jsonPath("$.price").value(499.9))
                .andExpect(jsonPath("$.stock").value(10));

        assertEquals(1, productRepository.count());
    }

    @Test
    @DisplayName("POST /product with invalid data returns 400")
    void createProductInvalid() throws Exception {
        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "", "sku": "KEY-001", "price": 499.9, "stock": 10}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("name is required"));

        assertEquals(0, productRepository.count());
    }

    @Test
    @DisplayName("POST /product with malformed JSON returns 400")
    void createProductMalformedJson() throws Exception {
        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed JSON request"));
    }

    @Test
    @DisplayName("POST /product with an existing SKU returns 409")
    void createProductDuplicateSku() throws Exception {
        productRepository.save(new ProductModel(null, "Keyboard", "KEY-001", 499.9, 10));

        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(KEYBOARD_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A product with this SKU already exists"));
    }

    @Test
    @DisplayName("GET /product returns all products")
    void findAllProducts() throws Exception {
        productRepository.save(new ProductModel(null, "Keyboard", "KEY-001", 499.9, 10));
        productRepository.save(new ProductModel(null, "Mouse", "MOU-001", 199.9, 25));

        mockMvc.perform(get("/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /product/{id} returns the product")
    void findProductById() throws Exception {
        ProductModel saved = productRepository.save(new ProductModel(null, "Keyboard", "KEY-001", 499.9, 10));

        mockMvc.perform(get("/product/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("KEY-001"));
    }

    @Test
    @DisplayName("GET /product/{id} with an unknown ID returns 404")
    void findProductByIdNotFound() throws Exception {
        mockMvc.perform(get("/product/{id}", "unknown-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    @DisplayName("PUT /product/{id} updates the product")
    void updateProduct() throws Exception {
        ProductModel saved = productRepository.save(new ProductModel(null, "Keyboard", "KEY-001", 499.9, 10));

        mockMvc.perform(put("/product/{id}", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Keyboard RGB", "sku": "KEY-001", "price": 599.9, "stock": 8}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Keyboard RGB"));

        assertEquals("Keyboard RGB", productRepository.findById(saved.getId()).orElseThrow().getName());
    }

    @Test
    @DisplayName("DELETE /product/{id} deletes the product and returns 204")
    void deleteProduct() throws Exception {
        ProductModel saved = productRepository.save(new ProductModel(null, "Keyboard", "KEY-001", 499.9, 10));

        mockMvc.perform(delete("/product/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        assertEquals(0, productRepository.count());
    }

}
