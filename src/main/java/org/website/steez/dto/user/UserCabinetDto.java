package org.website.steez.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCabinetDto {
    private String username;
    private String email;
    private String avatar;
    private Set<AddressDto> addresses;
}
