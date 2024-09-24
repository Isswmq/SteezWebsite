package org.website.steez.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.simpleframework.xml.core.Validate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.website.steez.controller.request.CartRequest;
import org.website.steez.dto.user.CartItemViewDto;
import org.website.steez.mapper.user.cartItem.CartItemViewMapper;
import org.website.steez.model.user.User;
import org.website.steez.service.CartItemService;
import org.website.steez.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cartItem")
@Tag(name = "Shopping Cart Controller", description = "Shopping Cart API")
public class ShoppingCartController {

    private final CartItemService cartItemService;
    private final UserService userService;
    private final CartItemViewMapper cartItemViewMapper;

    @GetMapping()
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<CartItemViewDto>> viewCart(@AuthenticationPrincipal User principalUser) {
        User user = userService.findById(principalUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<CartItemViewDto> cartItems = cartItemService.findCartItemsByUserId(user.getId());
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> addToCart(@AuthenticationPrincipal User principalUser,@Validated @RequestBody CartRequest request) {
        User user =  userService.findById(principalUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        cartItemService.addToCart(user.getId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok("Product added to cart");
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> updateQuantity(@AuthenticationPrincipal User principalUser, @Validated @RequestBody CartRequest request) {
        User user =  userService.findById(principalUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        cartItemService.updateQuantity(user.getId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok("Quantity updated");
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> removeFromCart(@AuthenticationPrincipal User principalUser, @RequestBody CartRequest request) {
        User user =  userService.findById(principalUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        cartItemService.removeFromCart(user.getId(), request.getProductId());
        return ResponseEntity.ok("Product removed from cart");
    }
}
