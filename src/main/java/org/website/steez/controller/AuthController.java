package org.website.steez.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.steez.auth.AuthenticationRequest;
import org.website.steez.auth.AuthenticationService;
import org.website.steez.auth.RegisterRequest;

@Tag(name = "Auth Controller", description = "Auth API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .headers(authenticationService.register(request))
                .build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .headers(authenticationService.authenticate(request))
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(authenticationService.refresh(request))
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(authenticationService.logout(request))
                .build();
    }
}
