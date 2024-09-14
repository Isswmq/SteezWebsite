package org.website.steez.auth;

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
public class RegisterRequest {

    @NotEmpty(message = "Username must not be empty.")
    private String username;

    @NotEmpty(message = "Email must not be empty.")
    @Pattern(regexp = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", message = "Email should be valid.")
    private String email;

    @NotEmpty(message = "Password must not be empty.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
            message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, and one digit.")
    private String password;
}
