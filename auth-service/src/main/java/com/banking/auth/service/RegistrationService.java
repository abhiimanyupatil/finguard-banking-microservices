package com.banking.auth.service;

import com.banking.auth.domain.User;
import com.banking.auth.dto.RegisterRequest;
import com.banking.auth.dto.RegisterResponse;
import com.banking.auth.exception.DuplicateUserException;
import com.banking.auth.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateUserException("username");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateUserException("email");
        }

        String phone = normalizeOptionalValue(request.phone());

        User user = new User(
                username,
                email,
                passwordEncoder.encode(request.password()),
                request.fullName().trim(),
                phone
        );

        User savedUser;

        try {
            savedUser = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateUserException("username or email");
        }

        return toResponse(savedUser);
    }

    private RegisterResponse toResponse(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }

    private String normalizeOptionalValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
