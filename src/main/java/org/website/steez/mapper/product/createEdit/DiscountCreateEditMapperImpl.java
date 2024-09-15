package org.website.steez.mapper.product.createEdit;

import org.springframework.stereotype.Component;
import org.website.steez.dto.product.DiscountCreateEditDto;
import org.website.steez.model.product.Discount;

@Component
public class DiscountCreateEditMapperImpl implements DiscountCreateEditMapper {

    @Override
    public DiscountCreateEditDto toDto(Discount entity) {
        return DiscountCreateEditDto
                .builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .discountPercent(entity.getDiscountPercent())
                .build();
    }

    @Override
    public Discount toEntity(DiscountCreateEditDto dto) {
        return Discount
                .builder()
                .description(dto.getDescription())
                .discountPercent(dto.getDiscountPercent())
                .name(dto.getName())
                .build();
    }
}
