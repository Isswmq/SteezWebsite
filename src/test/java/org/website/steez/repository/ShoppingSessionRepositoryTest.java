package org.website.steez.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import org.junit.jupiter.api.Test;
import org.website.steez.model.user.Role;
import org.website.steez.model.user.ShoppingSession;
import org.website.steez.model.user.User;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ShoppingSessionRepositoryTest {

    @Autowired
    private ShoppingSessionRepository shoppingSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void ShoppingSessionRepository_findByUser_ShouldReturnShoppingSession() {
        User user = userRepository.save(
                User.builder()
                        .username("testUser")
                        .email("test@example.com")
                        .role(Role.USER)
                        .isAccountNonLocked(true)
                        .build()
        );

        ShoppingSession shoppingSession = shoppingSessionRepository.save(
                ShoppingSession.builder()
                        .user(user)
                        .total(BigDecimal.ZERO)
                        .build()
        );

        Optional<ShoppingSession> foundSession = shoppingSessionRepository.findByUser(user);

        assertThat(foundSession.isPresent()).isTrue();
        assertThat(foundSession.get().getUser().getId()).isEqualTo(user.getId());
        assertThat(foundSession.get().getTotal()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void ShoppingSessionRepository_findByUser_ShouldReturnEmptyOptionalWhenNoSessionExists() {

        User user = userRepository.save(
                User.builder()
                        .username("testUserWithoutSession")
                        .email("testWithoutSession@example.com")
                        .role(Role.USER)
                        .isAccountNonLocked(true)
                        .build()
        );

        Optional<ShoppingSession> foundSession = shoppingSessionRepository.findByUser(user);

        assertThat(foundSession.isPresent()).isFalse();
    }

    @Test
    public void ShoppingSessionRepository_findByUser_ShouldReturnEmptyOptionalForNullUser() {
        assertThat(shoppingSessionRepository.findByUser(null)).isEmpty();
    }

    @Test
    public void ShoppingSessionRepository_findByUser_ShouldReturnEmptyForMultipleUsersWithoutSessions() {

        User user1 = userRepository.save(
                User.builder()
                        .username("testUser1")
                        .email("user1@example.com")
                        .role(Role.USER)
                        .isAccountNonLocked(true)
                        .build()
        );

        User user2 = userRepository.save(
                User.builder()
                        .username("testUser2")
                        .email("user2@example.com")
                        .role(Role.USER)
                        .isAccountNonLocked(true)
                        .build()
        );

        Optional<ShoppingSession> foundSession1 = shoppingSessionRepository.findByUser(user1);
        Optional<ShoppingSession> foundSession2 = shoppingSessionRepository.findByUser(user2);

        assertThat(foundSession1.isPresent()).isFalse();
        assertThat(foundSession2.isPresent()).isFalse();
    }
}
