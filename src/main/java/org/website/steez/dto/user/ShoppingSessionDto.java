package org.website.steez.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingSessionDto {
    private Long id;
    private UserOrderDto userDto;
    private BigDecimal total;
}
