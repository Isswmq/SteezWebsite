package org.website.steez.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.website.steez.model.product.Product;
import org.website.steez.model.user.CartItem;
import org.website.steez.model.user.ShoppingSession;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findBySessionAndProduct(ShoppingSession session, Product product);

    List<CartItem> findBySession (ShoppingSession session);

    List<CartItem> findBySessionUserId(Long userId);
}
