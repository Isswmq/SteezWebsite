package org.website.steez.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.website.steez.model.product.Discount;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class DiscountRepositoryTest {

    @Autowired
    private DiscountRepository discountRepository;

    @Test
    @DirtiesContext
    public void DiscountRepository_findById_ReturnOptionalDiscount() {
        Discount discount = Discount.builder()
                .name("Summer Sale")
                .description("50% off on summer collection")
                .discountPercent(50)
                .build();

        discount = discountRepository.save(discount);

        Optional<Discount> maybeDiscount = discountRepository.findById(discount.getId());

        assertThat(maybeDiscount).isPresent();
        assertThat(maybeDiscount.get().getId()).isEqualTo(discount.getId());
    }

    @Test
    @DirtiesContext
    public void DiscountRepository_findById_ReturnEmptyOptional() {
        Optional<Discount> maybeDiscount = discountRepository.findById(999);
        assertThat(maybeDiscount).isEmpty();
    }

    @Test
    @DirtiesContext
    public void DiscountRepository_findByName_ReturnOptionalDiscount() {
        Discount discount = Discount.builder()
                .name("Winter Sale")
                .description("30% off on winter collection")
                .discountPercent(30)
                .build();

        discountRepository.save(discount);

        Optional<Discount> maybeDiscount = discountRepository.findByName("Winter Sale");

        assertThat(maybeDiscount).isPresent();
        assertThat(maybeDiscount.get().getName()).isEqualTo(discount.getName());
    }

    @Test
    @DirtiesContext
    public void DiscountRepository_findByName_ReturnEmptyOptional() {
        Optional<Discount> maybeDiscount = discountRepository.findByName("Nonexistent Discount");
        assertThat(maybeDiscount).isEmpty();
    }

    @Test
    @DirtiesContext
    public void DiscountRepository_findAll_ReturnDiscounts() {
        Discount discount1 = Discount.builder()
                .name("Summer Sale")
                .description("50% off on summer collection")
                .discountPercent(50)
                .build();

        Discount discount2 = Discount.builder()
                .name("Winter Sale")
                .description("30% off on winter collection")
                .discountPercent(30)
                .build();

        discountRepository.save(discount1);
        discountRepository.save(discount2);

        List<Discount> discounts = discountRepository.findAll();

        assertThat(discounts).hasSize(2);
        assertThat(discounts).extracting(Discount::getName).containsExactlyInAnyOrder("Summer Sale", "Winter Sale");
    }
}
