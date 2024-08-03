package org.website.steez.mapper.user.avatar;

import org.springframework.stereotype.Component;
import org.website.steez.dto.UserAvatarDto;
import org.website.steez.model.user.UserAvatar;

@Component
public class UserAvatarMapperImpl implements UserAvatarMapper {

    @Override
    public UserAvatarDto toDto(UserAvatar entity) {
        return UserAvatarDto.builder()
                .file(entity.getFile())
                .build();
    }

    @Override
    public UserAvatar toEntity(UserAvatarDto dto) {
        return UserAvatar.builder()
                .file(dto.getFile())
                .build();
    }
}
