package org.website.steez.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.steez.dto.product.ProductCreateEditDto;
import org.website.steez.mapper.product.createEdit.ProductCreateEditMapper;
import org.website.steez.model.product.Discount;
import org.website.steez.model.product.Product;
import org.website.steez.repository.DiscountRepository;
import org.website.steez.repository.ProductRepository;
import org.website.steez.service.ProductService;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCreateEditMapper productCreateEditMapper;
    private final DiscountRepository discountRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "ProductService::findById", key = "#id")
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional
    @Caching(put = {
            @CachePut(value = "ProductService::findById", key = "#result.id"),
            @CachePut(value = "ProductService::findBySku", key = "#result.sku")
    })
    public Product create(ProductCreateEditDto dto) {
        Product product = productCreateEditMapper.toEntity(dto);
        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "ProductService::findBySku", key = "#sku")
    public Optional<Product> findBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    @Override
    @Transactional
    @Caching(put = {
            @CachePut(value = "ProductService::findById", key = "#result.id"),
            @CachePut(value = "ProductService::findBySku", key = "#result.sku")
    })
    public Product applyDiscount(String productSku, String discountName) {
        Product product = productRepository.findBySku(productSku)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with sku " + productSku));
        Discount discount = discountRepository.findByName(discountName)
                .orElseThrow(() -> new EntityNotFoundException("Discount not found with name " + discountName));

        BigDecimal originalPrice = product.getPrice();
        BigDecimal discountPercent = BigDecimal.valueOf(discount.getDiscountPercent());
        BigDecimal discountAmount = originalPrice.multiply(discountPercent).divide(BigDecimal.valueOf(100));
        BigDecimal newPrice = originalPrice.subtract(discountAmount);

        product.setDiscount(discount);
        product.setPrice(newPrice);
        return productRepository.save(product);
    }
}
