package com.assignment.ec8.config;

import com.assignment.ec8.entity.Product;
import com.assignment.ec8.entity.Role;
import com.assignment.ec8.entity.User;
import com.assignment.ec8.repository.ProductRepository;
import com.assignment.ec8.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mengisi data awal (seed) saat aplikasi pertama kali dijalankan:
 * - 2 akun demo: admin (ADMIN) dan user (USER)
 * - beberapa produk sampel untuk demo read-heavy & perbandingan response time
 */
@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build());
            userRepository.save(User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .build());
            log.info("Akun demo dibuat -> admin/admin123 (ADMIN), user/user123 (USER)");
        }

        if (productRepository.count() == 0) {
            productRepository.saveAll(List.of(
                    Product.builder().name("Laptop Gaming Pro").price(15000000.0).description("Laptop gaming high-end 16GB RAM").stock(10).build(),
                    Product.builder().name("Mouse Wireless").price(150000.0).description("Mouse bluetooth silent click").stock(50).build(),
                    Product.builder().name("Keyboard Mechanical").price(500000.0).description("Keyboard RGB mechanical switch").stock(30).build(),
                    Product.builder().name("Monitor 4K 27 Inch").price(3000000.0).description("Monitor IPS 4K UHD").stock(15).build(),
                    Product.builder().name("Flashdisk 64GB").price(80000.0).description("USB 3.0 flashdisk").stock(100).build(),
                    Product.builder().name("Webcam Full HD").price(450000.0).description("Webcam 1080p auto-focus").stock(25).build()
            ));
            log.info("6 produk sampel berhasil dibuat");
        }
    }
}
