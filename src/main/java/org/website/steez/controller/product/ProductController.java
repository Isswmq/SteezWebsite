package org.website.steez.controller.product;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.website.steez.controller.request.ApplyDiscountRequest;
import org.website.steez.dto.product.ProductCreateEditDto;
import org.website.steez.dto.product.ProductViewDto;
import org.website.steez.mapper.product.view.ProductViewMapper;
import org.website.steez.model.product.Product;
import org.website.steez.service.ProductService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
@Tag(name = "Product Controller", description = "Product API")
public class ProductController {

    private final ProductService productService;
    private final ProductViewMapper productViewMapper;

    @GetMapping
    public Page<ProductViewDto> getAllProducts(@PageableDefault(size = 10) Pageable pageable) {
        return productService.findAll(pageable)
                .map(productViewMapper::toDto);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductViewDto> getProductBySku(@PathVariable String sku) {
        return productService.findBySku(sku)
                .map(product -> ResponseEntity.ok(productViewMapper.toDto(product)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<ProductViewDto> createProduct(@Valid @RequestBody ProductCreateEditDto productDto) {
        Product product = productService.create(productDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productViewMapper.toDto(product));
    }

    @PostMapping("/sku/{sku}/apply-discount")
    public ResponseEntity<ProductViewDto> applyDiscount(@PathVariable String sku, @Valid @RequestBody ApplyDiscountRequest request) {
        Product product = productService.applyDiscount(sku, request.getDiscountName());
        return ResponseEntity.ok(productViewMapper.toDto(product));
    }
}
