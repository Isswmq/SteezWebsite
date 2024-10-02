package org.website.steez.mapper.user.order;

import org.springframework.stereotype.Component;
import org.website.steez.dto.user.UserOrderDto;
import org.website.steez.model.user.User;

@Component
public class UserOrderMapperImpl implements UserOrderMapper{

    @Override
    public UserOrderDto toDto(User entity) {
        return UserOrderDto.builder()
                .id(entity.getId())
                .session(entity.getShoppingSession())
                .email(entity.getEmail())
                .build();
    }

    @Override
    public User toEntity(UserOrderDto dto) {
        return null;
    }
}
