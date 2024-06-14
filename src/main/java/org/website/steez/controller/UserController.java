package org.website.steez.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.steez.model.User;
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
<<<<<<< HEAD


=======
>>>>>>> a11a1d9d2deb9b78a212a1331aa254167caa116b
}
