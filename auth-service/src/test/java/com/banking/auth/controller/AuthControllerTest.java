package com.banking.auth.controller;

import com.banking.auth.config.SecurityConfig;
import com.banking.auth.domain.Role;
import com.banking.auth.dto.RegisterRequest;
import com.banking.auth.dto.RegisterResponse;
import com.banking.auth.exception.DuplicateUserException;
import com.banking.auth.exception.GlobalExceptionHandler;
import com.banking.auth.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    @Test
    void shouldReturnCreatedForValidRegistration() throws Exception {
        RegisterResponse response = new RegisterResponse(
                101L,
                "new.user",
                "new.user@example.com",
                "New User",
                "+919876543210",
                Role.USER,
                true,
                LocalDateTime.of(2026, 7, 29, 23, 30)
        );

        when(
                registrationService.register(
                        any(RegisterRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "new.user",
                                          "email": "new.user@example.com",
                                          "password": "Secure@123",
                                          "fullName": "New User",
                                          "phone": "+919876543210"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(
                        jsonPath("$.username").value("new.user")
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("new.user@example.com")
                )
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(
                        jsonPath("$.password").doesNotExist()
                )
                .andExpect(
                        jsonPath("$.passwordHash").doesNotExist()
                );

        verify(registrationService)
                .register(any(RegisterRequest.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidRegistration()
            throws Exception {

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "x",
                                          "email": "invalid-email",
                                          "password": "weak",
                                          "fullName": ""
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.message")
                                .value("Request validation failed")
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.username"
                        ).exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.email"
                        ).exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.password"
                        ).exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.fullName"
                        ).exists()
                );

        verify(
                registrationService,
                never()
        ).register(any(RegisterRequest.class));
    }

    @Test
    void shouldReturnConflictForDuplicateEmail()
            throws Exception {

        when(
                registrationService.register(
                        any(RegisterRequest.class)
                )
        ).thenThrow(new DuplicateUserException("email"));

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "duplicate.user",
                                          "email": "duplicate@example.com",
                                          "password": "Secure@123",
                                          "fullName": "Duplicate User"
                                        }
                                        """)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "An account with this email already exists"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.email"
                        ).exists()
                );
    }
}
