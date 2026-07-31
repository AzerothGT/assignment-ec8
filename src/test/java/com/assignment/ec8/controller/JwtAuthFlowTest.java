package com.assignment.ec8.controller;

import com.assignment.ec8.dto.request.LoginRequest;
import com.assignment.ec8.dto.request.ProductRequest;
import com.assignment.ec8.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Menguji alur autentikasi JWT yang sebenarnya (bukan mock):
 * register -> login (ambil token) -> akses endpoint protected dengan token.
 */
@SpringBootTest
@ActiveProfiles("test")
class JwtAuthFlowTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void registerLoginThenAccessProtectedEndpoint_ShouldReturn200() throws Exception {
        registerUser("joko", "joko@example.com", "password123");

        String token = loginAndGetToken("joko", "password123");
        assertFalse(token.isEmpty(), "Token JWT tidak boleh kosong");

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void userToken_TryCreateProduct_ShouldReturn403() throws Exception {
        registerUser("sari", "sari@example.com", "password123");
        String token = loginAndGetToken("sari", "password123");

        ProductRequest request = ProductRequest.builder()
                .name("Produk Uji")
                .price(5000.0)
                .description("d")
                .stock(1)
                .build();

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer token-palsu-123"))
                .andExpect(status().isUnauthorized());
    }

    private void registerUser(String username, String email, String password) throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        LoginRequest login = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.path("data").path("token").asText();
    }
}
