package org.website.steez.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.website.steez.dto.product.ProductViewDto;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemViewDto implements Serializable {
    private ProductViewDto productViewDto;
    private Integer quantity;
}
