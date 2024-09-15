package org.website.steez.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplyDiscountRequest {
    @NotBlank(message = "Discount name must not be empty")
    private String discountName;
}
