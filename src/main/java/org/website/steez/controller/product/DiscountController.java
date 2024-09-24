package org.website.steez.controller.product;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.website.steez.dto.product.DiscountCreateEditDto;
import org.website.steez.dto.product.DiscountViewDto;

import org.website.steez.mapper.product.view.DiscountViewMapper;
import org.website.steez.model.product.Discount;
import org.website.steez.service.DiscountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/discount")
@Tag(name = "Discount Controller", description = "Discount API")
public class DiscountController {

    private final DiscountService discountService;
    private final DiscountViewMapper discountViewMapper;

    @GetMapping("/name/{name}")
    public ResponseEntity<DiscountViewDto> getByName(@PathVariable String name) {
        return discountService.findByName(name)
                .map(product -> ResponseEntity.ok(discountViewMapper.toDto(product)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<DiscountViewDto> getAllDiscounts() {
        return discountService.findAll()
                .stream().map(discountViewMapper::toDto).toList();
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiscountViewDto> createDiscount(@Valid @RequestBody DiscountCreateEditDto dto) {
        Discount discount = discountService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(discountViewMapper.toDto(discount));
    }
}
