package org.website.steez.dto;

import jakarta.validation.constraints.Email;
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
public class EmailDto {
    @NotBlank(message = "Recipient email must not be blank.")
    @Email(message = "Recipient email should be valid.")
    private String to;

    @NotBlank(message = "Subject must not be blank.")
    @Size(max = 100, message = "Subject must be less than 100 characters.")
    private String subject;

    @NotBlank(message = "Text must not be blank.")
    @Size(max = 1000, message = "Text must be less than 1000 characters.")
    private String text;
}