package org.website.steez.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.website.steez.dto.product.ProductViewDto;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemsViewDto {
    private ProductViewDto productViewDto;
    private Integer quantity;
    private BigDecimal price;
}
