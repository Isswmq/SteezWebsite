package org.website.steez.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.website.steez.model.product.Product;
import org.website.steez.model.user.ShoppingSession;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long id;
    private ShoppingSession session;
    private Product product;
    private Integer quantity;
}
