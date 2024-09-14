package org.website.steez.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCabinetDto {
    @NotBlank(message = "Username must not be blank.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    private String username;

    @NotBlank(message = "Email must not be blank.")
    @Email(message = "Email should be valid.")
    private String email;

    @Size(max = 255, message = "Avatar URL must not exceed 255 characters.")
    private String avatar;

    @NotEmpty(message = "Addresses must not be empty.")
    private Set<AddressDto> addresses;
}
