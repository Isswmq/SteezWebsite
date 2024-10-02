package org.website.steez.mapper.user.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.website.steez.dto.user.ShoppingSessionDto;
import org.website.steez.dto.user.UserOrderDto;
import org.website.steez.model.user.ShoppingSession;

@Component
@RequiredArgsConstructor
public class ShoppingSessionMapperImpl implements ShoppingSessionMapper{

    private final UserOrderMapper userOrderMapper;

    @Override
    public ShoppingSessionDto toDto(ShoppingSession entity) {
        UserOrderDto userDto = userOrderMapper.toDto(entity.getUser());

        return ShoppingSessionDto.builder()
                .id(entity.getId())
                .userDto(userDto)
                .total(entity.getTotal())
                .build();
    }

    @Override
    public ShoppingSession toEntity(ShoppingSessionDto dto) {
        return null;
    }
}
