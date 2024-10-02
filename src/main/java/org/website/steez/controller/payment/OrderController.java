package org.website.steez.controller.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.website.steez.controller.request.ConfirmPaymentRequest;
import org.website.steez.controller.response.StripeResponse;
import org.website.steez.dto.order.OrderDetailsViewDto;
import org.website.steez.mapper.order.OrderDetailsViewMapper;
import org.website.steez.mapper.user.order.UserOrderMapper;
import org.website.steez.model.order.OrderDetails;
import org.website.steez.model.user.User;
import org.website.steez.service.OrderService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Controller", description = "Order API")
public class OrderController {

    private final OrderService orderService;
    private final UserOrderMapper userOrderMapper;
    private final OrderDetailsViewMapper orderDetailsViewMapper;

    @PostMapping("/payment/create")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal User principalUser) {

        OrderDetails order = orderService.createOrder(userOrderMapper.toDto(principalUser));

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(order.getTotal().multiply(new BigDecimal(100)).longValue())
                .setCurrency("usd")
                .build();

        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.create(params);
        } catch (StripeException e) {
            throw new IllegalStateException("Error while creating PaymentIntent: " + e.getMessage());
        }

        OrderDetailsViewDto orderDto = orderDetailsViewMapper.toDto(order);

        Map<String, Object> response = new HashMap<>();
        response.put("order", orderDto);
        response.put("clientSecret", paymentIntent.getClientSecret());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment/{paymentIntentId}/confirm")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> confirmPayment(@PathVariable String paymentIntentId,
                                            @RequestBody ConfirmPaymentRequest request) {
        PaymentIntent paymentIntent;
        try {
            PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
                    .setPaymentMethod(request.getPaymentMethod())
                    .setReturnUrl("http://localhost:8080/payment-success")
                    .build();

            paymentIntent = PaymentIntent.retrieve(paymentIntentId).confirm(confirmParams);
        } catch (StripeException e) {
            throw new IllegalStateException("Error confirming payment: " + e.getMessage());
        }

        StripeResponse response = StripeResponse.builder()
                .body(paymentIntent.getDescription())
                .statusCode(paymentIntent.getStatus())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment/success")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> success(){
        return ResponseEntity.ok("Payment succeeded");
    }
}
