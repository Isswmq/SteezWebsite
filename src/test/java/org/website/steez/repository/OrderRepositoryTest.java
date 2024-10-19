package org.website.steez.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.website.steez.model.order.OrderDetails;
import org.website.steez.model.order.PaymentDetails;
import org.website.steez.model.user.Role;
import org.website.steez.model.user.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DirtiesContext
    public void OrderRepository_findByUserId_ReturnOrderDetailsList() {
        User user = User.builder()
                .username("steez")
                .email("steezhack@gmail.com")
                .role(Role.USER)
                .isAccountNonLocked(true)
                .build();
        userRepository.save(user);

        PaymentDetails paymentDetails = PaymentDetails.builder()
                .amount(new BigDecimal("100"))
                .provider("Stripe")
                .status("completed")
                .build();

        OrderDetails order = OrderDetails.builder()
                .user(user)
                .total(BigDecimal.valueOf(100.00))
                .paymentDetails(paymentDetails)
                .build();

        orderRepository.save(order);

        List<OrderDetails> orders = orderRepository.findByUserId(user.getId());
        assertThat(orders).isNotEmpty();
        assertThat(orders.get(0).getUser().getId()).isEqualTo(user.getId());
        assertThat(orders.get(0).getPaymentDetails()).isNotNull();
        assertThat(orders.get(0).getPaymentDetails().getProvider()).isEqualTo("Stripe");
    }

    @Test
    @DirtiesContext
    public void OrderRepository_findByUserId_ReturnEmptyList() {
        Long userId = 999L;

        List<OrderDetails> orders = orderRepository.findByUserId(userId);

        assertThat(orders).isEmpty();
    }

    @Test
    @DirtiesContext
    public void OrderRepository_saveOrder_ReturnSavedOrder() {
        User user = User.builder()
                .username("steez")
                .email("steezhack@gmail.com")
                .role(Role.USER)
                .isAccountNonLocked(true)
                .build();
        userRepository.save(user);

        OrderDetails order = OrderDetails.builder()
                .id(1)
                .user(user)
                .total(BigDecimal.valueOf(100.00))
                .build();
        order.setUser(user);

        OrderDetails savedOrder = orderRepository.save(order);

        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getUser().getId()).isEqualTo(user.getId());
        assertThat(savedOrder.getTotal()).isEqualTo(BigDecimal.valueOf(100.00));
    }

    @Test
    @DirtiesContext
    public void OrderRepository_findAll_ReturnMoreThanOneOrder() {
        User user = User.builder()
                .username("steez")
                .email("steezhack@gmail.com")
                .role(Role.USER)
                .isAccountNonLocked(true)
                .build();
        userRepository.save(user);

        OrderDetails order1 = OrderDetails.builder()
                .user(user)
                .total(BigDecimal.valueOf(100.00))
                .build();
        OrderDetails order2 = OrderDetails.builder()
                .user(user)
                .total(BigDecimal.valueOf(200.00))
                .build();

        orderRepository.save(order1);
        orderRepository.save(order2);

        List<OrderDetails> orders = orderRepository.findAll();

        assertThat(orders).isNotNull();
        assertThat(orders.size()).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    public void OrderRepository_deleteOrder_SuccessfullyRemovesOrder() {
        User user = User.builder()
                .id(1L)
                .username("steez")
                .email("steezhack@gmail.com")
                .role(Role.USER)
                .isAccountNonLocked(true)
                .build();
        userRepository.save(user);

        OrderDetails order = OrderDetails.builder()
                .user(user)
                .total(BigDecimal.valueOf(100.00))
                .build();
        OrderDetails savedOrder = orderRepository.save(order);
        orderRepository.delete(savedOrder);

        Optional<OrderDetails> deletedOrder = orderRepository.findById(savedOrder.getId());
        assertThat(deletedOrder).isNotPresent();
    }
}
