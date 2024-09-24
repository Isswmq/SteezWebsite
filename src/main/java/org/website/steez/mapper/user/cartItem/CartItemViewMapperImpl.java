package org.website.steez.mapper.user.cartItem;

import org.springframework.stereotype.Component;
import org.website.steez.dto.product.ProductViewDto;
import org.website.steez.dto.user.CartItemViewDto;
import org.website.steez.model.product.Product;
import org.website.steez.model.user.CartItem;

@Component
public class CartItemViewMapperImpl implements CartItemViewMapper{

    @Override
    public CartItemViewDto toDto(CartItem entity) {
        ProductViewDto productViewDto = ProductViewDto.builder()
                .name(entity.getProduct().getName())
                .sku(entity.getProduct().getSku())
                .category(entity.getProduct().getCategory())
                .price(entity.getProduct().getPrice())
                .description(entity.getProduct().getDescription())
                .discount(entity.getProduct().getDiscount())
                .build();

        return CartItemViewDto.
                builder()
                .productViewDto(productViewDto)
                .quantity(entity.getQuantity())
                .build();
    }

    @Override
    public CartItem toEntity(CartItemViewDto dto) {
        Product product = Product.builder()
                .name(dto.getProductViewDto().getName())
                .sku(dto.getProductViewDto().getSku())
                .category(dto.getProductViewDto().getCategory())
                .price(dto.getProductViewDto().getPrice())
                .description(dto.getProductViewDto().getDescription())
                .discount(dto.getProductViewDto().getDiscount())
                .build();

        return CartItem
                .builder()
                .product(product)
                .quantity(dto.getQuantity())
                .build();
    }
}
