package org.website.steez.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.website.steez.model.product.Category;
import org.website.steez.model.product.Product;
import org.website.steez.model.user.CartItem;
import org.website.steez.model.user.Role;
import org.website.steez.model.user.ShoppingSession;
import org.website.steez.model.user.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CartItemRepositoryTest {

    @Autowired
    private ShoppingSessionRepository shoppingSessionRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void CartItemRepository_findBySessionAndProduct_ShouldReturnOptionalCartItem() {
        User user = userRepository.save(
                User.builder()
                    .username("steez")
                    .email("steezhack@gmail.com")
                    .role(Role.USER)
                    .isAccountNonLocked(true)
                    .build()
        );

        Product product = productRepository.save(
                Product.builder()
                        .sku("999_999")
                        .name("steez")
                        .price(new BigDecimal(100))
                        .category(Category.HOODIES)
                        .build()
        );

        ShoppingSession shoppingSession = shoppingSessionRepository.save(
                ShoppingSession.builder()
                    .user(user)
                    .total(BigDecimal.ZERO)
                    .build()
        );

        CartItem cartItem = cartItemRepository.save(
                CartItem.builder()
                    .session(shoppingSession)
                    .product(product)
                    .quantity(2)
                    .build()
        );

        Optional<CartItem> foundCartItem = cartItemRepository.findBySessionAndProduct(shoppingSession, product);

        assertThat(foundCartItem.isPresent()).isTrue();
        assertThat(cartItem.getQuantity()).isEqualTo(foundCartItem.get().getQuantity());
    }

    @Test
    public void CartItemRepository_findBySessionAndProduct_ShouldReturnEmptyOptional() {
        User user = userRepository.save(
                User.builder()
                        .username("testUser")
                        .email("test@example.com")
                        .role(Role.USER)
                        .isAccountNonLocked(true)
                        .build()
        );

        Product product = productRepository.save(
                Product.builder()
                        .sku("123_456")
                        .name("testProduct")
                        .price(new BigDecimal(100))
                        .category(Category.HOODIES)
                        .build()
        );

        ShoppingSession shoppingSession = shoppingSessionRepository.save(
                ShoppingSession.builder()
                        .user(user)
                        .total(BigDecimal.ZERO)
                        .build()
        );

        Optional<CartItem> foundCartItem = cartItemRepository.findBySessionAndProduct(shoppingSession, product);

        assertThat(foundCartItem.isPresent()).isFalse();
    }

    @Test
    public void CartItemRepository_findBySession_ShouldReturnListOfCartItems() {
        User user = userRepository.save(
                User.builder()
                        .username("steez")
                        .email("steezhack@gmail.com")
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

        Product product1 = productRepository.save(
                Product.builder()
                        .sku("123_123")
                        .name("product1")
                        .price(new BigDecimal(50))
                        .category(Category.HOODIES)
                        .build()
        );

        Product product2 = productRepository.save(
                Product.builder()
                        .sku("456_456")
                        .name("product2")
                        .price(new BigDecimal(75))
                        .category(Category.HOODIES)
                        .build()
        );

        CartItem cartItem1 = cartItemRepository.save(
                CartItem.builder()
                        .session(shoppingSession)
                        .product(product1)
                        .quantity(2)
                        .build()
        );

        CartItem cartItem2 = cartItemRepository.save(
                CartItem.builder()
                        .session(shoppingSession)
                        .product(product2)
                        .quantity(2)
                        .build()
        );

        List<CartItem> cartItems = cartItemRepository.findBySession(shoppingSession);

        assertThat(cartItems.size()).isEqualTo(2);
    }

    @Test
    public void CartItemRepository_findBySession_ShouldReturnEmptyList() {
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

        List<CartItem> cartItems = cartItemRepository.findBySession(shoppingSession);

        assertThat(cartItems).isEmpty();
    }

    @Test
    public void CartItemRepository_findBySessionUserId_ShouldReturnListOfCartItems() {
        User user = userRepository.save(
                User.builder()
                        .username("steez")
                        .email("steezhack@gmail.com")
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

        Product product = productRepository.save(
                Product.builder()
                        .sku("999_999")
                        .name("steez")
                        .price(new BigDecimal(100))
                        .category(Category.HOODIES)
                        .build()
        );

        CartItem cartItem = cartItemRepository.save(
                CartItem.builder()
                        .session(shoppingSession)
                        .product(product)
                        .quantity(2)
                        .build()
        );

        List<CartItem> cartItems = cartItemRepository.findBySessionUserId(user.getId());

        assertThat(cartItems.size()).isEqualTo(1);
        assertThat(cartItem.getQuantity()).isEqualTo(cartItems.get(0).getQuantity());
    }

    @Test
    public void CartItemRepository_findBySessionUserId_ShouldReturnEmptyListWhenNoItems() {
        User user = userRepository.save(
                User.builder()
                        .username("testUser")
                        .email("test@example.com")
                        .role(Role.USER)
                        .isAccountNonLocked(true)
                        .build()
        );

        List<CartItem> cartItems = cartItemRepository.findBySessionUserId(user.getId());

        assertThat(cartItems).isEmpty();
    }
}
