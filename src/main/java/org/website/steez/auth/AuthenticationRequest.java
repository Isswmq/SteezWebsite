package org.website.steez.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    @NotBlank(message = "Email must not be empty.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotBlank(message = "Password must not be empty.")
    @Pattern(regexp = "^(?=.*[A-Z]).{8,}$",
            message = "Password must be at least 8 characters long and include at least one uppercase letter.")
    private String password;
}
