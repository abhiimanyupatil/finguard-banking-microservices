package com.banking.auth.dto;

import com.banking.auth.domain.Role;

import java.time.LocalDateTime;

public record RegisterResponse(
        Long id,
        String username,
        String email,
        String fullName,
        String phone,
        Role role,
        boolean active,
        LocalDateTime createdAt
) {
}
