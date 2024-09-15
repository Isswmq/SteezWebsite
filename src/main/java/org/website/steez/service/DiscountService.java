package org.website.steez.service;

import org.website.steez.dto.product.DiscountCreateEditDto;
import org.website.steez.model.product.Discount;

import java.util.List;
import java.util.Optional;

public interface DiscountService {

    Optional<Discount> findById(Integer id);

    Optional<Discount> findByName(String name);

    List<Discount> findAll();

    Discount create (DiscountCreateEditDto dto);
}
