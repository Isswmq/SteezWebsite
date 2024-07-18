package org.website.steez.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.website.steez.model.User;
import org.website.steez.security.ChangePasswordRequest;
import org.website.steez.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/cabinet")
    public User getUserInfo(@AuthenticationPrincipal User user) {
        return user;
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, @AuthenticationPrincipal User user) {
        userService.changePassword(request, user);
        return ResponseEntity.ok().build();
    }
}
