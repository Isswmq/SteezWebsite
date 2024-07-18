package org.website.steez.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.website.steez.exception.UserNotFoundException;
import org.website.steez.model.User;
import org.website.steez.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<User> getAllUsers() {
        Pageable pageable = PageRequest.of(0, 20);
        return userService.findAll(pageable);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/lock/{id}")
    public User blockUser(@PathVariable Long id) {
        userService.lockOrUnlockUser(id, false);
        return userService.findById(id).orElseThrow(() -> new UserNotFoundException("user not found"));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/unlock/{id}")
    public User unlockUser(@PathVariable Long id) {
        userService.lockOrUnlockUser(id, true);
        return userService.findById(id).orElseThrow(() -> new UserNotFoundException("user not found"));
    }
}
