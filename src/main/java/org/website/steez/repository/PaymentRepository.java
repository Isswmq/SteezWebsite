package org.website.steez.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.website.steez.model.order.PaymentDetails;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentDetails, Integer> {
}
