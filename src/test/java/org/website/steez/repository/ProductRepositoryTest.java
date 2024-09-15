package org.website.steez.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.website.steez.model.product.Category;
import org.website.steez.model.product.Product;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void ProductRepository_findById_ReturnOptionalProduct() {
        Product product = productRepository.save(
                Product.builder()
                        .sku("999_999")
                        .name("skeet")
                        .price(new BigDecimal(100))
                        .category(Category.HOODIES)
                        .build()
        );

        Optional<Product> maybeProduct = productRepository.findById(product.getId());

        assertThat(maybeProduct).isPresent();
        assertThat(maybeProduct.get().getId()).isEqualTo(product.getId());
        assertThat(maybeProduct.get().getName()).isEqualTo("skeet");
        assertThat(maybeProduct.get().getCategory()).isEqualTo(Category.HOODIES);
    }

    @Test
    public void ProductRepository_findById_ReturnEmptyOptional() {
        Long productId = -1L;
        Optional<Product> maybeProduct = productRepository.findById(productId);
        assertThat(maybeProduct).isEmpty();
    }

    @Test
    public void ProductRepository_findBySku_ReturnOptionalProduct() {
        Product product = productRepository.save(
                Product.builder()
                        .sku("999_999")
                        .name("skeet")
                        .price(new BigDecimal(100))
                        .category(Category.HOODIES)
                        .build()
        );

        Optional<Product> maybeProduct = productRepository.findBySku(product.getSku());

        assertThat(maybeProduct).isPresent();
        assertThat(maybeProduct.get().getSku()).isEqualTo(product.getSku());
        assertThat(maybeProduct.get().getName()).isEqualTo("skeet");
    }

    @Test
    public void ProductRepository_findBySku_ReturnEmptyOptional() {
        String sku = "steez";
        Optional<Product> maybeProduct = productRepository.findBySku(sku);
        assertThat(maybeProduct).isEmpty();
    }

    @Test
    public void ProductRepository_findAll_ReturnMoreThenOneProduct() {
        Product product1 = Product
                .builder()
                .sku("999_999")
                .name("skeet")
                .price(new BigDecimal(100))
                .category(Category.HOODIES)
                .build();

        Product product2 = Product
                .builder()
                .sku("99_99")
                .name("skeet")
                .price(new BigDecimal(100))
                .category(Category.HOODIES)
                .build();

        productRepository.saveAll(List.of(product1, product2));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> result = productRepository.findAll(pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getSku()).isEqualTo("999_999");
        assertThat(result.getContent().get(1).getSku()).isEqualTo("99_99");
    }
}

