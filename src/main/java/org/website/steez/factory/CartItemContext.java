package org.website.steez.factory;

import org.website.steez.model.product.Product;
import org.website.steez.model.user.CartItem;
import org.website.steez.model.user.ShoppingSession;
import org.website.steez.model.user.User;

public record CartItemContext(User user, Product product, ShoppingSession shoppingSession, CartItem cartItem) {
}
