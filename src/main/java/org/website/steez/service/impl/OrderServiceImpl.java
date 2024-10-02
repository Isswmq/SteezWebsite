package org.website.steez.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.steez.dto.user.UserOrderDto;
import org.website.steez.model.order.OrderDetails;
import org.website.steez.model.order.OrderItems;
import org.website.steez.model.order.PaymentDetails;
import org.website.steez.model.user.CartItem;
import org.website.steez.model.user.ShoppingSession;
import org.website.steez.model.user.User;
import org.website.steez.repository.*;
import org.website.steez.service.OrderService;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingSessionRepository shoppingSessionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderDetails createOrder(UserOrderDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ShoppingSession session = shoppingSessionRepository.findByUserId(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Shopping session not found"));

        List<CartItem> cartItems = cartItemRepository.findBySession(session);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("The cart is empty. It is possible to create an order.");
        }

        BigDecimal totalAmount = session.getTotal();

        OrderDetails orderDetails = OrderDetails.builder()
                .user(user)
                .total(totalAmount)
                .build();

        OrderDetails savedOrder = orderRepository.save(orderDetails);

        PaymentDetails paymentDetails = PaymentDetails.builder()
                .amount(totalAmount)
                .provider("STRIPE")
                .status("pending")
                .orderId(savedOrder.getId())
                .build();

        paymentRepository.save(paymentDetails);

        savedOrder.setPaymentDetails(paymentDetails);

        Set<OrderItems> orderItems = new HashSet<>();
        for (CartItem cartItem : cartItems) {
            OrderItems orderItem = OrderItems.builder()
                    .orderDetails(savedOrder)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getProduct().getPrice())
                    .build();
            orderItems.add(orderItem);
        }
        savedOrder.setOrderItems(orderItems);

        orderRepository.save(savedOrder);

        return savedOrder;
    }
}
