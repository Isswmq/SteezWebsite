package org.website.steez.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.website.steez.model.product.Category;
import org.website.steez.model.product.Discount;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewDto implements Serializable {
    private String name;
    private String sku;
    private Category category;
    private BigDecimal price;
    private String description;
    private Discount discount;
}
