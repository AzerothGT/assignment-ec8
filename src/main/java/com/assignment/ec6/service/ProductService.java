package com.assignment.ec6.service;

import com.assignment.ec6.dto.request.ProductRequest;
import com.assignment.ec6.dto.response.ProductResponse;
import com.assignment.ec6.entity.Product;
import com.assignment.ec6.exception.BadRequestException;
import com.assignment.ec6.exception.ResourceNotFoundException;
import com.assignment.ec6.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .stock(request.getStock())
                .build();

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk dengan ID " + id + " tidak ditemukan"));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setStock(request.getStock());

        Product updated = productRepository.save(product);
        return mapToResponse(updated);
    }

    @Transactional
    public ProductResponse updateStockOnSale(Long id, Integer soldQuantity) {
        if (soldQuantity == null || soldQuantity <= 0) {
            throw new BadRequestException("Jumlah stok yang terjual tidak boleh 0 atau negatif");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk dengan ID " + id + " tidak ditemukan"));

        if (product.getStock() == 0) {
            throw new BadRequestException("Stok produk tidak boleh 0");
        }

        int remainingStock = product.getStock() - soldQuantity;
        if (remainingStock < 0) {
            throw new BadRequestException("Stok tidak boleh menjadi negatif. Stok saat ini: " + product.getStock() + ", jumlah terjual: " + soldQuantity);
        }

        product.setStock(remainingStock);
        Product updated = productRepository.save(product);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk dengan ID " + id + " tidak ditemukan"));

        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk dengan ID " + id + " tidak ditemukan"));
        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .stock(product.getStock())
                .build();
    }
}
