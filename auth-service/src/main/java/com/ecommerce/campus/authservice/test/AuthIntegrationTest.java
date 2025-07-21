package com.ecommerce.campus.authservice.test;

import com.ecommerce.campus.authservice.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthIntegrationTest {

    @Container
    static MSSQLServerContainer<?> msSqlContainer = new MSSQLServerContainer<>("postgres:15")
            .withDatabaseName("auth_db_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", msSqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", msSqlContainer::getUsername);
        registry.add("spring.datasource.password", msSqlContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterLoginAndRefreshToken() throws Exception {
        // 1. Register new user
        RegisterRequest registerRequest = new RegisterRequest(
                "testuser",
                "test@example.com",
                "password123",
                "Test",
                "Dad",
                "Mom"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        // 2. Login
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        TokenResponse tokenResponse = objectMapper.readValue(response, TokenResponse.class);

        // 3. Refresh token
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(tokenResponse.refreshToken());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").value(tokenResponse.refreshToken()));
    }
}