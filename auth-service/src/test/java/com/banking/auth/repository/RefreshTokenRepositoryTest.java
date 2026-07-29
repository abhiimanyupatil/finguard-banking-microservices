package com.banking.auth.repository;

import com.banking.auth.domain.RefreshToken;
import com.banking.auth.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RefreshTokenRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void shouldSaveAndFindRefreshTokenByHash() {
        User user = createAndSaveUser(
                "token.user",
                "token.user@example.com"
        );

        LocalDateTime referenceTime = LocalDateTime.now();

        RefreshToken token = new RefreshToken(
                user,
                "a".repeat(64),
                referenceTime.plusDays(7)
        );

        RefreshToken savedToken =
                refreshTokenRepository.saveAndFlush(token);

        assertThat(savedToken.getId()).isNotNull();
        assertThat(savedToken.getCreatedAt()).isNotNull();
        assertThat(savedToken.isRevoked()).isFalse();
        assertThat(savedToken.isUsableAt(referenceTime.plusDays(1)))
                .isTrue();

        RefreshToken foundToken = refreshTokenRepository
                .findByTokenHash("a".repeat(64))
                .orElseThrow();

        assertThat(foundToken.getId()).isEqualTo(savedToken.getId());
        assertThat(
                refreshTokenRepository.existsByTokenHash(
                        "a".repeat(64)
                )
        ).isTrue();

        List<RefreshToken> activeTokens =
                refreshTokenRepository
                        .findAllByUser_IdAndRevokedAtIsNull(
                                user.getId()
                        );

        assertThat(activeTokens)
                .extracting(RefreshToken::getId)
                .contains(savedToken.getId());
    }

    @Test
    void shouldFindExpiredTokensAndExcludeRevokedTokens() {
        User user = createAndSaveUser(
                "expiry.user",
                "expiry.user@example.com"
        );

        LocalDateTime referenceTime = LocalDateTime.now();

        RefreshToken expiredToken = new RefreshToken(
                user,
                "b".repeat(64),
                referenceTime.minusMinutes(1)
        );

        RefreshToken revokedToken = new RefreshToken(
                user,
                "c".repeat(64),
                referenceTime.plusDays(7)
        );

        revokedToken.revokeAt(referenceTime);

        refreshTokenRepository.saveAllAndFlush(
                List.of(expiredToken, revokedToken)
        );

        List<RefreshToken> expiredTokens =
                refreshTokenRepository
                        .findAllByExpiresAtBefore(referenceTime);

        assertThat(expiredTokens)
                .extracting(RefreshToken::getTokenHash)
                .contains("b".repeat(64));

        List<RefreshToken> nonRevokedTokens =
                refreshTokenRepository
                        .findAllByUser_IdAndRevokedAtIsNull(
                                user.getId()
                        );

        assertThat(nonRevokedTokens)
                .extracting(RefreshToken::getTokenHash)
                .contains("b".repeat(64))
                .doesNotContain("c".repeat(64));

        assertThat(expiredToken.isExpiredAt(referenceTime)).isTrue();
        assertThat(revokedToken.isRevoked()).isTrue();
        assertThat(revokedToken.isUsableAt(referenceTime)).isFalse();
    }

    private User createAndSaveUser(
            String username,
            String email
    ) {
        User user = new User(
                username,
                email,
                "hashed-password",
                "Repository Test User",
                null
        );

        return userRepository.saveAndFlush(user);
    }
}
