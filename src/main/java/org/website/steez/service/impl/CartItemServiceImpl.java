package org.website.steez.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.steez.dto.user.CartItemViewDto;
import org.website.steez.factory.CartItemContext;
import org.website.steez.factory.CartItemFactory;
import org.website.steez.mapper.user.cartItem.CartItemViewMapper;
import org.website.steez.model.user.CartItem;
import org.website.steez.model.user.ShoppingSession;
import org.website.steez.model.user.User;
import org.website.steez.repository.CartItemRepository;
import org.website.steez.repository.ShoppingSessionRepository;
import org.website.steez.service.CartItemService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemFactory cartItemFactory;
    private final CartItemRepository cartItemRepository;
    private final ShoppingSessionRepository shoppingSessionRepository;
    private final CartItemViewMapper cartItemViewMapper;

    @Override
    @Transactional
    @CacheEvict(value = "CartItemService::findCartItemsByUserId", key = "#userId")
    public void addToCart(Long userId, Long productId, int quantity) {
        CartItemContext context = cartItemFactory.create(userId, productId);
        CartItem cartItem = context.cartItem();

        if (cartItem.getId() != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            log.info("Updated quantity for productId {} in userId {} to {}", productId, userId, cartItem.getQuantity());
        } else {
            cartItem.setSession(context.shoppingSession());
            cartItem.setProduct(context.product());
            cartItem.setQuantity(quantity);
            log.info("Added new productId {} to userId {} with quantity {}", productId, userId, quantity);
        }

        cartItemRepository.save(cartItem);
        updateTotal(context.shoppingSession());
    }

    @Override
    @Transactional
    @CacheEvict(value = "CartItemService::findCartItemsByUserId", key = "#userId")
    public void updateQuantity(Long userId, Long productId, int quantity) {
        CartItemContext context = cartItemFactory.create(userId, productId);
        CartItem cartItem = context.cartItem();
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        updateTotal(context.shoppingSession());
        log.info("Updated quantity for productId {} in userId {} to {}", productId, userId, quantity);
    }

    @Override
    @Transactional
    @CacheEvict(value = "CartItemService::findCartItemsByUserId", key = "#userId")
    public void removeFromCart(Long userId, Long productId) {
        CartItemContext context = cartItemFactory.create(userId, productId);
        CartItem cartItem = context.cartItem();
        cartItemRepository.delete(cartItem);
        updateTotal(context.shoppingSession());
        log.info("Removed productId {} from userId {}", productId, userId);
    }

    @Override
    @Transactional
    public ShoppingSession createShoppingSession(User user) {
        ShoppingSession shoppingSession = new ShoppingSession();
        shoppingSession.setUser(user);
        shoppingSession.setTotal(BigDecimal.ZERO);
        ShoppingSession savedSession = shoppingSessionRepository.save(shoppingSession);
        log.info("Created shopping session for userId {}", user.getId());
        return savedSession;
    }

    @Override
    @Transactional
    public void updateTotal(ShoppingSession shoppingSession) {
        List<CartItem> cartItems = cartItemRepository.findBySession(shoppingSession);

        BigDecimal total = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        shoppingSession.setTotal(total);
        shoppingSessionRepository.save(shoppingSession);
        log.info("Updated total for shopping session userId {} to {}", shoppingSession.getUser().getId(), total);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "CartItemService::findCartItemsByUserId", key = "#userId")
    public List<CartItemViewDto> findCartItemsByUserId(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findBySessionUserId(userId);
        log.info("Retrieved cart items for userId {}", userId);
        return cartItems.stream().map(cartItemViewMapper::toDto).toList();
    }
}

