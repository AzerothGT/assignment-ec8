package com.assignment.ec8.service;

import com.assignment.ec8.dto.request.ProductRequest;
import com.assignment.ec8.dto.response.ProductResponse;
import com.assignment.ec8.entity.Product;
import com.assignment.ec8.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
class CacheTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames().forEach(name -> {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
        productRepository.deleteAll();
    }

    @Test
    void getAllProducts_ShouldPopulateProductsCache() {
        productRepository.save(Product.builder()
                .name("Produk A").price(1000.0).description("d1").stock(10).build());

        productService.getAllProducts();

        assertNotNull(cacheManager.getCache("products").get(SimpleKey.EMPTY),
                "Cache 'products' harus terisi setelah getAllProducts()");
    }

    @Test
    void getProductById_ShouldPopulateCacheWithKeyId() {
        Product product = productRepository.save(Product.builder()
                .name("Produk B").price(2000.0).description("d2").stock(5).build());

        productService.getProductById(product.getId());

        assertNotNull(cacheManager.getCache("productById").get(product.getId()),
                "Cache 'productById' harus terisi dengan key = id produk");
    }

    @Test
    void updateProduct_ShouldRefreshProductByIdCacheAndEvictList() {
        Product product = productRepository.save(Product.builder()
                .name("Produk Lama").price(1000.0).description("d3").stock(5).build());

        ProductRequest request = ProductRequest.builder()
                .name("Produk Baru")
                .price(2500.0)
                .description("d3-updated")
                .stock(8)
                .build();

        ProductResponse updated = productService.updateProduct(product.getId(), request);

        Cache.ValueWrapper cached = cacheManager.getCache("productById").get(product.getId());
        assertNotNull(cached, "Cache 'productById' harus berisi data setelah @CachePut");
        assertEquals(updated, cached.get(), "Data di cache harus sama dengan data terbaru di database");

        assertNull(cacheManager.getCache("products").get(SimpleKey.EMPTY),
                "Cache 'products' (list) harus di-evict setelah update");
    }

    @Test
    void deleteProduct_ShouldEvictCaches() {
        Product product = productRepository.save(Product.builder()
                .name("Produk C").price(3000.0).description("d4").stock(3).build());

        productService.getProductById(product.getId());
        productService.getAllProducts();

        assertNotNull(cacheManager.getCache("productById").get(product.getId()));
        assertNotNull(cacheManager.getCache("products").get(SimpleKey.EMPTY));

        productService.deleteProduct(product.getId());

        assertNull(cacheManager.getCache("productById").get(product.getId()),
                "Cache 'productById' harus di-evict setelah delete");
        assertNull(cacheManager.getCache("products").get(SimpleKey.EMPTY),
                "Cache 'products' harus di-evict setelah delete");
    }
}
