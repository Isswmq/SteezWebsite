package org.website.steez.service;

import org.website.steez.dto.user.UserOrderDto;
import org.website.steez.model.order.OrderDetails;

public interface OrderService {

    OrderDetails createOrder(UserOrderDto userDto);
}
