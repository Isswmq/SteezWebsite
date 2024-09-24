package org.website.steez.factory;

import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.website.steez.model.product.Product;
import org.website.steez.model.user.CartItem;
import org.website.steez.model.user.ShoppingSession;
import org.website.steez.model.user.User;
import org.website.steez.repository.CartItemRepository;
import org.website.steez.repository.ProductRepository;
import org.website.steez.repository.ShoppingSessionRepository;
import org.website.steez.repository.UserRepository;

import java.math.BigDecimal;

@Data
@Component
@RequiredArgsConstructor
public class CartItemFactory {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ShoppingSessionRepository shoppingSessionRepository;
    private final CartItemRepository cartItemRepository;

    public CartItemContext create(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + productId));

        ShoppingSession shoppingSession = shoppingSessionRepository.findByUser(user)
                .orElseGet(() -> createShoppingSession(user));

        CartItem cartItem = cartItemRepository.findBySessionAndProduct(shoppingSession, product)
                .orElseGet(CartItem::new);

        return new CartItemContext(user, product, shoppingSession, cartItem);
    }

    private ShoppingSession createShoppingSession(User user) {
        ShoppingSession shoppingSession = new ShoppingSession();
        shoppingSession.setUser(user);
        shoppingSession.setTotal(BigDecimal.ZERO);
        return shoppingSessionRepository.save(shoppingSession);
    }
}
