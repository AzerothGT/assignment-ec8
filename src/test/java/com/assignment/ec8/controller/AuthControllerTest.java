package com.assignment.ec8.controller;

import com.assignment.ec8.dto.request.LoginRequest;
import com.assignment.ec8.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRegister_Success_ShouldReturn201WithRoleUser() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("alice")
                .email("alice@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.username", is("alice")))
                .andExpect(jsonPath("$.data.email", is("alice@example.com")))
                .andExpect(jsonPath("$.data.role", is("USER")))
                .andExpect(jsonPath("$.data.token").doesNotExist());
    }

    @Test
    void testRegister_DuplicateUsername_ShouldReturn400() throws Exception {
        RegisterRequest first = RegisterRequest.builder()
                .username("budi")
                .email("budi@example.com")
                .password("password123")
                .build();
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isCreated());

        RegisterRequest duplicate = RegisterRequest.builder()
                .username("budi")
                .email("budi-lain@example.com")
                .password("password123")
                .build();
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("BAD_REQUEST")));
    }

    @Test
    void testRegister_InvalidEmail_ShouldReturn400() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("citra")
                .email("bukan-email")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("BAD_REQUEST")));
    }

    @Test
    void testLogin_Success_ShouldReturnJwtToken() throws Exception {
        RegisterRequest register = RegisterRequest.builder()
                .username("dodi")
                .email("dodi@example.com")
                .password("password123")
                .build();
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequest login = LoginRequest.builder()
                .username("dodi")
                .password("password123")
                .build();
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.data.role", is("USER")));
    }

    @Test
    void testLogin_WrongPassword_ShouldReturn401() throws Exception {
        RegisterRequest register = RegisterRequest.builder()
                .username("eko")
                .email("eko@example.com")
                .password("password123")
                .build();
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequest login = LoginRequest.builder()
                .username("eko")
                .password("salah-password")
                .build();
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("UNAUTHORIZED")));
    }
}
