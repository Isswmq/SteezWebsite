package org.website.steez.model.order;

import jakarta.persistence.*;
import lombok.*;
import org.website.steez.model.user.User;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_details")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total", nullable = false)
    private BigDecimal total;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private PaymentDetails paymentDetails;

    @Builder.Default
    @OneToMany(mappedBy = "orderDetails", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItems> orderItems = new HashSet<>();
}
