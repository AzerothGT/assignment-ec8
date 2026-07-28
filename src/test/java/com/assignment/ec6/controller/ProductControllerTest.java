package com.assignment.ec6.controller;

import com.assignment.ec6.dto.request.ProductRequest;
import com.assignment.ec6.dto.request.UpdateStockRequest;
import com.assignment.ec6.entity.Product;
import com.assignment.ec6.exception.GlobalExceptionHandler;
import com.assignment.ec6.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class ProductControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ProductController productController;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private ProductRepository productRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void testAddProduct_Success() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("Laptop Gaming")
                .price(15000000.0)
                .description("High end gaming laptop")
                .stock(10)
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.name", is("Laptop Gaming")))
                .andExpect(jsonPath("$.data.price", is(15000000.0)))
                .andExpect(jsonPath("$.data.stock", is(10)));
    }

    @Test
    void testAddProduct_InvalidPrice_ShouldReturn400() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("Laptop Gaming")
                .price(0.0)
                .description("Invalid price laptop")
                .stock(10)
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("BAD_REQUEST")));
    }

    @Test
    void testAddProduct_InvalidStock_ShouldReturn400() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("Laptop Gaming")
                .price(100.0)
                .description("Invalid stock laptop")
                .stock(-5)
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("BAD_REQUEST")));
    }

    @Test
    void testEditProduct_Success() throws Exception {
        Product existing = productRepository.save(Product.builder()
                .name("Mouse Wireless")
                .price(150000.0)
                .description("Mouse bluetooth")
                .stock(20)
                .build());

        ProductRequest updateRequest = ProductRequest.builder()
                .name("Mouse Wireless Gaming")
                .price(200000.0)
                .description("Updated description")
                .stock(25)
                .build();

        mockMvc.perform(put("/api/products/" + existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.name", is("Mouse Wireless Gaming")))
                .andExpect(jsonPath("$.data.price", is(200000.0)))
                .andExpect(jsonPath("$.data.stock", is(25)));
    }

    @Test
    void testEditProduct_NotFound_ShouldReturn404() throws Exception {
        ProductRequest updateRequest = ProductRequest.builder()
                .name("Mouse Wireless")
                .price(150000.0)
                .description("Description")
                .stock(20)
                .build();

        mockMvc.perform(put("/api/products/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("NOT_FOUND")));
    }

    @Test
    void testUpdateStockOnSale_Success() throws Exception {
        Product existing = productRepository.save(Product.builder()
                .name("Keyboard Mechanical")
                .price(500000.0)
                .description("RGB Keyboard")
                .stock(15)
                .build());

        UpdateStockRequest sellRequest = UpdateStockRequest.builder()
                .soldQuantity(3)
                .build();

        mockMvc.perform(patch("/api/products/" + existing.getId() + "/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sellRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.stock", is(12)));
    }

    @Test
    void testUpdateStockOnSale_ExceedsStock_ShouldReturn400() throws Exception {
        Product existing = productRepository.save(Product.builder()
                .name("Monitor 4K")
                .price(3000000.0)
                .description("27 inch monitor")
                .stock(2)
                .build());

        UpdateStockRequest sellRequest = UpdateStockRequest.builder()
                .soldQuantity(5)
                .build();

        mockMvc.perform(patch("/api/products/" + existing.getId() + "/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sellRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("BAD_REQUEST")));
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        Product existing = productRepository.save(Product.builder()
                .name("Flashdisk 64GB")
                .price(80000.0)
                .description("USB 3.0")
                .stock(50)
                .build());

        mockMvc.perform(delete("/api/products/" + existing.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)));

        assertFalse(productRepository.existsById(existing.getId()));
    }

    @Test
    void testDeleteProduct_NotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/products/8888"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("NOT_FOUND")));
    }
}
