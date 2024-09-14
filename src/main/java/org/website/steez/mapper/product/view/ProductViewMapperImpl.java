package org.website.steez.mapper.product.view;

import org.springframework.stereotype.Component;
import org.website.steez.dto.product.ProductViewDto;
import org.website.steez.model.product.Product;

@Component
public class ProductViewMapperImpl implements ProductViewMapper{

    @Override
    public ProductViewDto toDto(Product entity) {
        return ProductViewDto
                .builder()
                .category(entity.getCategory())
                .description(entity.getDescription())
                .name(entity.getName())
                .price(entity.getPrice())
                .discount(entity.getDiscount())
                .sku(entity.getSku())
                .build();
    }

    @Override
    public Product toEntity(ProductViewDto dto) {
        return Product
                .builder()
                .category(dto.getCategory())
                .description(dto.getDescription())
                .name(dto.getName())
                .price(dto.getPrice())
                .discount(dto.getDiscount())
                .sku(dto.getSku())
                .build();
    }
}
