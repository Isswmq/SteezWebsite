package org.website.steez.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.website.steez.dto.product.ProductCreateEditDto;
import org.website.steez.model.product.Product;

import java.util.Optional;

public interface ProductService {

    Optional<Product> findById(Long id);

    Page<Product> findAll(Pageable pageable);

    Product create(ProductCreateEditDto dto);

    Optional<Product> findBySku(String sku);

    Product applyDiscount(String productSku, String discountName);
}
