package org.website.steez.mapper;

import org.mapstruct.Mapper;
import org.website.steez.dto.UserAvatarDto;
import org.website.steez.model.user.UserAvatar;


@Mapper(componentModel = "spring")
public interface UserAvatarMapper extends Mappable<UserAvatar, UserAvatarDto> {
}
