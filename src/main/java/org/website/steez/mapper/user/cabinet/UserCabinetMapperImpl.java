package org.website.steez.mapper.user.cabinet;

import org.springframework.stereotype.Component;
import org.website.steez.dto.user.AddressDto;
import org.website.steez.dto.user.UserCabinetDto;
import org.website.steez.model.user.Address;
import org.website.steez.model.user.User;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserCabinetMapperImpl implements UserCabinetMapper {

    public AddressDto toAddressDTO(Address address) {
        return AddressDto.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .portalCode(address.getPostalCode())
                .country(address.getCountry())
                .addressLine(address.getAddressLine())
                .build();
    }

    @Override
    public UserCabinetDto toDto(User entity) {
        Set<AddressDto> addressDTOs = entity.getAddresses().stream()
                .map(this::toAddressDTO)
                .collect(Collectors.toSet());

        return UserCabinetDto.builder()
                .email(entity.getEmail())
                .username(entity.getUsername())
                .addresses(addressDTOs)
                .avatar(entity.getAvatar())
                .build();
    }

    @Override
    public User toEntity(UserCabinetDto dto) {
        return null;
    }
}
