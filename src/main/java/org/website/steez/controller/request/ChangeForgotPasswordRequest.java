package org.website.steez.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeForgotPasswordRequest {
    @NotBlank(message = "New password must not be empty")
    @Size(min = 8, message = "New password must be at least 8 characters long")
    private String newPassword;

    @NotBlank(message = "Confirmation password must not be empty")
    @Size(min = 8, message = "Confirmation password must be at least 8 characters long")
    private String confirmationPassword;
}
