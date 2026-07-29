package com.banking.auth.service;

import com.banking.auth.domain.User;
import com.banking.auth.dto.RegisterRequest;
import com.banking.auth.dto.RegisterResponse;
import com.banking.auth.exception.DuplicateUserException;
import com.banking.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void shouldRegisterUserWithNormalizedDataAndHashedPassword() {
        RegisterRequest request = new RegisterRequest(
                "  Test.User  ",
                "  TEST.USER@Example.com  ",
                "Secure@123",
                "  Test User  ",
                "  +919876543210  "
        );

        when(
                userRepository.existsByUsernameIgnoreCase(
                        "Test.User"
                )
        ).thenReturn(false);

        when(
                userRepository.existsByEmailIgnoreCase(
                        "test.user@example.com"
                )
        ).thenReturn(false);

        when(passwordEncoder.encode("Secure@123"))
                .thenReturn("$2a$12$encoded-password-hash");

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 7, 29, 23, 30);

        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);

                    ReflectionTestUtils.setField(
                            user,
                            "id",
                            100L
                    );

                    ReflectionTestUtils.setField(
                            user,
                            "createdAt",
                            createdAt
                    );

                    ReflectionTestUtils.setField(
                            user,
                            "updatedAt",
                            createdAt
                    );

                    return user;
                });

        RegisterResponse response =
                registrationService.register(request);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.username()).isEqualTo("Test.User");
        assertThat(response.email())
                .isEqualTo("test.user@example.com");
        assertThat(response.fullName()).isEqualTo("Test User");
        assertThat(response.phone()).isEqualTo("+919876543210");
        assertThat(response.role().name()).isEqualTo("USER");
        assertThat(response.active()).isTrue();
        assertThat(response.createdAt()).isEqualTo(createdAt);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).saveAndFlush(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getPasswordHash())
                .isEqualTo("$2a$12$encoded-password-hash");

        assertThat(savedUser.getPasswordHash())
                .isNotEqualTo("Secure@123");

        verify(passwordEncoder).encode("Secure@123");
    }

    @Test
    void shouldRejectDuplicateUsername() {
        RegisterRequest request = validRequest();

        when(
                userRepository.existsByUsernameIgnoreCase(
                        "existing.user"
                )
        ).thenReturn(true);

        assertThatThrownBy(
                () -> registrationService.register(request)
        )
                .isInstanceOf(DuplicateUserException.class)
                .hasMessage(
                        "An account with this username already exists"
                );

        verify(
                userRepository,
                never()
        ).existsByEmailIgnoreCase(anyString());

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void shouldRejectDuplicateEmail() {
        RegisterRequest request = validRequest();

        when(
                userRepository.existsByUsernameIgnoreCase(
                        "existing.user"
                )
        ).thenReturn(false);

        when(
                userRepository.existsByEmailIgnoreCase(
                        "existing@example.com"
                )
        ).thenReturn(true);

        assertThatThrownBy(
                () -> registrationService.register(request)
        )
                .isInstanceOf(DuplicateUserException.class)
                .hasMessage(
                        "An account with this email already exists"
                );

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    private RegisterRequest validRequest() {
        return new RegisterRequest(
                "existing.user",
                "existing@example.com",
                "Secure@123",
                "Existing User",
                null
        );
    }
}
