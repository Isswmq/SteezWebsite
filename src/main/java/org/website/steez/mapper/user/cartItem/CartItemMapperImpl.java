package org.website.steez.mapper.user.cartItem;

import org.springframework.stereotype.Component;
import org.website.steez.dto.user.CartItemDto;
import org.website.steez.model.user.CartItem;

@Component
public class CartItemMapperImpl implements CartItemMapper{

    @Override
    public CartItemDto toDto(CartItem entity) {
        return CartItemDto
                .builder()
                .id(entity.getId())
                .product(entity.getProduct())
                .quantity(entity.getQuantity())
                .session(entity.getSession())
                .build();
    }

    @Override
    public CartItem toEntity(CartItemDto dto) {
        return CartItem
                .builder()
                .quantity(dto.getQuantity())
                .product(dto.getProduct())
                .session(dto.getSession())
                .id(dto.getId())
                .build();
    }
}
