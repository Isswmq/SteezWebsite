package org.website.steez.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.website.steez.model.order.OrderDetails;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderDetails, Integer> {

    List<OrderDetails> findByUserId(Long userId);
}
