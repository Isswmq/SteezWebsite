package org.website.steez.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsViewDto {
    private BigDecimal total;
    private PaymentDetailsViewDto paymentDetailsDto;
    private Set<OrderItemsViewDto> orderItems;
    private String clientSecret;
}
