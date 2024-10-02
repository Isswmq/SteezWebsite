package org.website.steez.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.website.steez.exception.UserNotFoundException;
import org.website.steez.model.user.User;
import org.website.steez.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Controller", description = "Admin API")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        Pageable pageable = PageRequest.of(0, 20);
        return userService.findAll(pageable);
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id).orElseThrow(() -> new UserNotFoundException("user not found"));
    }

    @PutMapping("/lock/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User blockUser(@PathVariable Long id) {
        userService.updateAccountLockStatusById(id, false);
        return userService.findById(id).orElseThrow(() -> new UserNotFoundException("user not found"));
    }

    @PutMapping("/unlock/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User unlockUser(@PathVariable Long id) {
        userService.updateAccountLockStatusById(id, true);
        return userService.findById(id).orElseThrow(() -> new UserNotFoundException("user not found"));
    }
}
