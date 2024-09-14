package org.website.steez.mapper.product.createEdit;

import org.springframework.stereotype.Component;
import org.website.steez.dto.product.ProductCreateEditDto;
import org.website.steez.model.product.Product;

@Component
public class ProductCreateEditMapperImpl implements ProductCreateEditMapper{

    @Override
    public ProductCreateEditDto toDto(Product entity) {
        return ProductCreateEditDto
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
    public Product toEntity(ProductCreateEditDto dto) {
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
