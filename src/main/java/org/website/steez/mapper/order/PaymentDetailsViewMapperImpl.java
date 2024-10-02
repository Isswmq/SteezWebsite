package org.website.steez.mapper.order;

import org.springframework.stereotype.Component;
import org.website.steez.dto.order.PaymentDetailsViewDto;
import org.website.steez.model.order.PaymentDetails;

@Component
public class PaymentDetailsViewMapperImpl implements PaymentDetailsViewMapper{
    @Override
    public PaymentDetailsViewDto toDto(PaymentDetails entity) {
        return PaymentDetailsViewDto.builder()
                .amount(entity.getAmount())
                .provider(entity.getProvider())
                .status(entity.getStatus())
                .build();
    }

    @Override
    public PaymentDetails toEntity(PaymentDetailsViewDto dto) {
        return null;
    }
}
