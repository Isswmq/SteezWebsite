package org.website.steez.mapper.product.view;

import org.springframework.stereotype.Component;
import org.website.steez.dto.product.DiscountViewDto;
import org.website.steez.model.product.Discount;

@Component
public class DiscountViewMapperImpl implements DiscountViewMapper{

    @Override
    public DiscountViewDto toDto(Discount entity) {
        return DiscountViewDto
                .builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .discountPercent(entity.getDiscountPercent())
                .build();
    }

    @Override
    public Discount toEntity(DiscountViewDto dto) {
        return Discount
                .builder()
                .description(dto.getDescription())
                .discountPercent(dto.getDiscountPercent())
                .name(dto.getName())
                .build();
    }
}
