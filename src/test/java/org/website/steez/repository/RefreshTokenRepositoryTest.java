package org.website.steez.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.website.steez.model.RefreshToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    public void RefreshTokenRepository_findByToken_ShouldReturnOptionalRefreshTokenWhenTokenExists() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername("steez");
        refreshToken.setExpiryDate(Instant.now().plusMillis(999_999L));
        String token = UUID.randomUUID().toString();
        refreshToken.setToken(token);
        refreshTokenRepository.save(refreshToken);

        Optional<RefreshToken> maybeRefreshToken = refreshTokenRepository.findByToken(token);
        assertThat(maybeRefreshToken).isPresent();
    }

    @Test
    public void RefreshTokenRepository_findByToken_ShouldReturnEmptyOptionalWhenTokenNotFound() {
        String nonExistentToken = UUID.randomUUID().toString();
        Optional<RefreshToken> maybeRefreshToken = refreshTokenRepository.findByToken(nonExistentToken);
        assertThat(maybeRefreshToken).isEmpty();
    }

    @Test
    public void RefreshTokenRepository_findByToken_ShouldReturnEmptyOptionalWhenTokenIsCaseSensitive() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername("steez");
        refreshToken.setExpiryDate(Instant.now().plusMillis(999_999L));
        String token = UUID.randomUUID().toString().toLowerCase();
        refreshToken.setToken(token);
        refreshTokenRepository.save(refreshToken);

        Optional<RefreshToken> maybeRefreshToken = refreshTokenRepository.findByToken(token.toUpperCase());
        assertThat(maybeRefreshToken).isEmpty();
    }

    @Test
    public void RefreshTokenRepository_findByToken_ShouldReturnEmptyOptionalWhenTokenIsNull() {
        Optional<RefreshToken> maybeRefreshToken = refreshTokenRepository.findByToken(null);
        assertThat(maybeRefreshToken).isEmpty();
    }

    @Test
    public void RefreshTokenRepository_deleteByUsername_ShouldDeleteRefreshTokenByUsername(){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername("steez");
        refreshToken.setExpiryDate(Instant.now().plusMillis(999_999L));
        String token = UUID.randomUUID().toString();
        refreshToken.setToken(token);
        refreshTokenRepository.save(refreshToken);

        Optional<RefreshToken> savedToken = refreshTokenRepository.findByToken(token);
        assertThat(savedToken).isPresent();

        refreshTokenRepository.deleteByUsername("steez");

        Optional<RefreshToken> deletedToken = refreshTokenRepository.findByToken(token);
        assertThat(deletedToken).isEmpty();
    }

    @Test
    public void RefreshTokenRepository_deleteByUsername_ShouldDoNothingWhenUsernameDoesNotExistDuringDelete() {
        refreshTokenRepository.deleteByUsername("nonexistentUser");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername("steez");
        refreshToken.setExpiryDate(Instant.now().plusMillis(999_999L));
        String token = UUID.randomUUID().toString();
        refreshToken.setToken(token);
        refreshTokenRepository.save(refreshToken);

        Optional<RefreshToken> maybeRefreshToken = refreshTokenRepository.findByToken(token);
        assertThat(maybeRefreshToken).isPresent();
    }

    @Test
    public void RefreshTokenRepository_deleteByUsername_ShouldNotDeleteAnythingWhenUsernameIsNull() {
        refreshTokenRepository.deleteByUsername(null);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername("steez");
        refreshToken.setExpiryDate(Instant.now().plusMillis(999_999L));
        String token = UUID.randomUUID().toString();
        refreshToken.setToken(token);
        refreshTokenRepository.save(refreshToken);

        Optional<RefreshToken> maybeRefreshToken = refreshTokenRepository.findByToken(token);
        assertThat(maybeRefreshToken).isPresent();
    }
}
