package org.website.steez.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.website.steez.dto.product.ProductCreateEditDto;
import org.website.steez.mapper.product.createEdit.ProductCreateEditMapper;
import org.website.steez.model.product.Discount;
import org.website.steez.model.product.Product;
import org.website.steez.repository.DiscountRepository;
import org.website.steez.repository.ProductRepository;
import org.website.steez.service.impl.ProductServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCreateEditMapper productCreateEditMapper;

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void findById_shouldReturnProduct_whenProductExists() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.findById(productId);

        assertThat(result).isPresent();
        assertThat(productId).isEqualTo(result.get().getId());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void findById_shouldReturnEmpty_whenProductDoesNotExist() {
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Optional<Product> result = productService.findById(productId);

        assertThat(result.isPresent()).isFalse();
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void findAll_shouldReturnPageOfProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = new Product();
        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<Product> result = productService.findAll(pageable);

        assertThat(result).isNotNull();
        assertThat(1).isEqualTo(result.getTotalElements());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void create_shouldSaveProduct() {
        ProductCreateEditDto dto = new ProductCreateEditDto();
        Product product = new Product();

        when(productCreateEditMapper.toEntity(dto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(product);
        verify(productCreateEditMapper, times(1)).toEntity(dto);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void findBySku_shouldReturnProduct_whenProductExists() {
        String sku = "123ABC";
        Product product = new Product();
        product.setSku(sku);

        when(productRepository.findBySku(sku)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.findBySku(sku);

        assertThat(result.isPresent()).isTrue();
        assertThat(sku).isEqualTo(result.get().getSku());
        verify(productRepository, times(1)).findBySku(sku);
    }

    @Test
    void applyDiscount_shouldUpdatePriceAndApplyDiscount() {
        String productSku = "123ABC";
        String discountName = "SUMMER_SALE";
        Product product = new Product();
        product.setSku(productSku);
        product.setPrice(BigDecimal.valueOf(100));

        Discount discount = new Discount();
        discount.setName(discountName);
        discount.setDiscountPercent(20);

        when(productRepository.findBySku(productSku)).thenReturn(Optional.of(product));
        when(discountRepository.findByName(discountName)).thenReturn(Optional.of(discount));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.applyDiscount(productSku, discountName);

        assertThat(result).isNotNull();
        assertThat(BigDecimal.valueOf(80)).isEqualTo(result.getPrice());
        assertThat(discount).isEqualTo(result.getDiscount());

        verify(productRepository, times(1)).findBySku(productSku);
        verify(discountRepository, times(1)).findByName(discountName);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void applyDiscount_shouldThrowException_whenProductNotFound() {
        String productSku = "123ABC";
        String discountName = "SUMMER_SALE";

        when(productRepository.findBySku(productSku)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            productService.applyDiscount(productSku, discountName);
        });

        assertThat("Product not found with sku " + productSku).isEqualTo(exception.getMessage());
        verify(productRepository, times(1)).findBySku(productSku);
        verify(discountRepository, never()).findByName(discountName);
    }

    @Test
    void applyDiscount_shouldThrowException_whenDiscountNotFound() {
        String productSku = "123ABC";
        String discountName = "SUMMER_SALE";
        Product product = new Product();
        product.setSku(productSku);

        when(productRepository.findBySku(productSku)).thenReturn(Optional.of(product));
        when(discountRepository.findByName(discountName)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            productService.applyDiscount(productSku, discountName);
        });

        assertThat("Discount not found with name " + discountName).isEqualTo(exception.getMessage());
        verify(productRepository, times(1)).findBySku(productSku);
        verify(discountRepository, times(1)).findByName(discountName);
    }
}
