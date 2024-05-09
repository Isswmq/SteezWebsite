package org.website.steez.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.website.steez.service.UserService;

@RestController("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

}
