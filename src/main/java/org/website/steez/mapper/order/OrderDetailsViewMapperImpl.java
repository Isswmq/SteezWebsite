package org.website.steez.mapper.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.website.steez.dto.order.OrderDetailsViewDto;
import org.website.steez.dto.order.OrderItemsViewDto;
import org.website.steez.dto.order.PaymentDetailsViewDto;
import org.website.steez.model.order.OrderDetails;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderDetailsViewMapperImpl implements OrderDetailsViewMapper{

    private final PaymentDetailsViewMapper paymentDetailsViewMapper;
    private final OrderItemsViewMapper orderItemsViewMapper;

    @Override
    public OrderDetailsViewDto toDto(OrderDetails entity) {
        PaymentDetailsViewDto paymentDetailsDto = paymentDetailsViewMapper.toDto(entity.getPaymentDetails());

        Set<OrderItemsViewDto> orderItems = entity.getOrderItems()
                .stream()
                .map(orderItemsViewMapper::toDto)
                .collect(Collectors.toSet());

        return OrderDetailsViewDto.builder()
                .orderItems(orderItems)
                .paymentDetailsDto(paymentDetailsDto)
                .total(entity.getTotal())
                .build();
    }

    @Override
    public OrderDetails toEntity(OrderDetailsViewDto dto) {
        return null;
    }
}
