package com.banking.auth.repository;

import com.banking.auth.domain.Role;
import com.banking.auth.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserIgnoringEmailAndUsernameCase() {
        User user = new User(
                "abhimanyu.patil",
                "Abhimanyu@example.com",
                "hashed-password",
                "Abhimanyu Patil",
                "9999999999"
        );

        User savedUser = userRepository.saveAndFlush(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
        assertThat(savedUser.isActive()).isTrue();
        assertThat(savedUser.getFailedLoginAttempts()).isZero();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();

        User foundByEmail = userRepository
                .findByEmailIgnoreCase("ABHIMANYU@EXAMPLE.COM")
                .orElseThrow();

        User foundByUsername = userRepository
                .findByUsernameIgnoreCase("ABHIMANYU.PATIL")
                .orElseThrow();

        assertThat(foundByEmail.getId()).isEqualTo(savedUser.getId());
        assertThat(foundByUsername.getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void shouldCheckWhetherEmailAndUsernameExistIgnoringCase() {
        User user = new User(
                "test.user",
                "test.user@example.com",
                "hashed-password",
                "Test User",
                null
        );

        userRepository.saveAndFlush(user);

        assertThat(
                userRepository.existsByEmailIgnoreCase(
                        "TEST.USER@EXAMPLE.COM"
                )
        ).isTrue();

        assertThat(
                userRepository.existsByUsernameIgnoreCase(
                        "TEST.USER"
                )
        ).isTrue();

        assertThat(
                userRepository.existsByEmailIgnoreCase(
                        "missing@example.com"
                )
        ).isFalse();
    }
}
