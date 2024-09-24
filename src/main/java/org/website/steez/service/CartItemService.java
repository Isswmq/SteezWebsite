package org.website.steez.service;

import org.website.steez.dto.user.CartItemViewDto;
import org.website.steez.model.user.ShoppingSession;
import org.website.steez.model.user.User;

import java.util.List;

public interface CartItemService {

    void addToCart(Long userId, Long productId, int quantity);

    void updateQuantity(Long userId, Long productId, int quantity);

    void removeFromCart(Long userId, Long productId);

    ShoppingSession createShoppingSession(User user);

    void updateTotal(ShoppingSession shoppingSession);

    List<CartItemViewDto> findCartItemsByUserId (Long userId);
}
