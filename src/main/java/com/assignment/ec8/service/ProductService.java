package com.assignment.ec8.service;

import com.assignment.ec8.dto.request.ProductRequest;
import com.assignment.ec8.dto.response.ProductResponse;
import com.assignment.ec8.entity.Product;
import com.assignment.ec8.exception.BadRequestException;
import com.assignment.ec8.exception.ResourceNotFoundException;
import com.assignment.ec8.repository.ProductRepository;
import com.assignment.ec8.validation.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RequestValidator requestValidator;

    @Transactional
    @CacheEvict(cacheNames = "products", allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {
        requestValidator.validate(request);

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
    @CachePut(cacheNames = "productById", key = "#id")
    @CacheEvict(cacheNames = "products", allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        requestValidator.validate(request);

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
    @CachePut(cacheNames = "productById", key = "#id")
    @CacheEvict(cacheNames = "products", allEntries = true)
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
    @CacheEvict(cacheNames = {"productById", "products"}, allEntries = true)
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk dengan ID " + id + " tidak ditemukan"));

        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "productById", key = "#id")
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk dengan ID " + id + " tidak ditemukan"));
        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "products")
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
