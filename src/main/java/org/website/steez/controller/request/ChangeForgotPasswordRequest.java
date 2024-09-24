package org.website.steez.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeForgotPasswordRequest {
    private String newPassword;
    private String confirmationPassword;
}
