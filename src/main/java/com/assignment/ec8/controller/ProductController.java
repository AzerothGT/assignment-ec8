package com.assignment.ec8.controller;

import com.assignment.ec8.dto.request.ProductRequest;
import com.assignment.ec8.dto.request.UpdateStockRequest;
import com.assignment.ec8.dto.response.ApiResponse;
import com.assignment.ec8.dto.response.ProductResponse;
import com.assignment.ec8.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ApiResponse.success("Produk berhasil ditambahkan", response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        ProductResponse response = productService.updateProduct(id, request);
        return ApiResponse.success("Produk berhasil diperbarui", response);
    }

    @PatchMapping("/{id}/sell")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductResponse> updateStockOnSale(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStockRequest request
    ) {
        ProductResponse response = productService.updateStockOnSale(id, request.getSoldQuantity());
        return ApiResponse.success("Stok produk berhasil diperbarui", response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success("Produk berhasil dihapus", null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> responseList = productService.getAllProducts();
        return ApiResponse.success(responseList);
    }
}
