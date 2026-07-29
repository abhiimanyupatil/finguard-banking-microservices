package com.banking.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Username is required")
        @Size(
                min = 3,
                max = 50,
                message = "Username must contain between 3 and 50 characters"
        )
        @Pattern(
                regexp = "^[A-Za-z0-9._-]+$",
                message = "Username may contain letters, numbers, dots, underscores and hyphens only"
        )
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email address is invalid")
        @Size(
                max = 100,
                message = "Email must not exceed 100 characters"
        )
        String email,

        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                max = 72,
                message = "Password must contain between 8 and 72 characters"
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "Password must contain an uppercase letter, lowercase letter, number and special character"
        )
        String password,

        @NotBlank(message = "Full name is required")
        @Size(
                min = 2,
                max = 100,
                message = "Full name must contain between 2 and 100 characters"
        )
        String fullName,

        @Pattern(
                regexp = "^$|^\\+?[1-9]\\d{7,14}$",
                message = "Phone number must contain between 8 and 15 digits"
        )
        String phone
) {
}
