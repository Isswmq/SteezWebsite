package org.website.steez.mapper.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.website.steez.dto.order.OrderItemsViewDto;
import org.website.steez.dto.product.ProductViewDto;
import org.website.steez.mapper.product.view.ProductViewMapper;
import org.website.steez.model.order.OrderItems;

@Component
@RequiredArgsConstructor
public class OrderItemsViewMapperImpl implements OrderItemsViewMapper{

    private final ProductViewMapper productViewMapper;

    @Override
    public OrderItemsViewDto toDto(OrderItems entity) {
        ProductViewDto productViewDto = productViewMapper.toDto(entity.getProduct());

        return OrderItemsViewDto.builder()
                .price(entity.getPrice())
                .quantity(entity.getQuantity())
                .productViewDto(productViewDto)
                .build();
    }

    @Override
    public OrderItems toEntity(OrderItemsViewDto dto) {
        return null;
    }
}
