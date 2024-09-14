package org.website.steez.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    @NotNull(message = "Street cannot be null")
    @Size(min = 1, max = 100, message = "Street must be between 1 and 100 characters")
    private String street;

    @NotNull(message = "City cannot be null")
    @Size(min = 1, max = 100, message = "City must be between 1 and 100 characters")
    private String city;

    @NotNull(message = "State cannot be null")
    @Size(min = 1, max = 100, message = "State must be between 1 and 100 characters")
    private String state;

    @NotNull(message = "Postal code cannot be null")
    @Pattern(regexp = "\\d{5}(-\\d{4})?", message = "Postal code should be a valid format (e.g., 12345 or 12345-6789)")
    private String portalCode;

    @NotNull(message = "Country cannot be null")
    @Size(min = 1, max = 100, message = "Country must be between 1 and 100 characters")
    private String country;

    @Size(max = 255, message = "Address line should be at most 255 characters long")
    private String addressLine;
}
