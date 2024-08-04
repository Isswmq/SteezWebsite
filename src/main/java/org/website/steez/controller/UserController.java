package org.website.steez.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.website.steez.dto.user.UserAvatarDto;
import org.website.steez.dto.user.UserCabinetDto;
import org.website.steez.mapper.user.avatar.UserAvatarMapper;
import org.website.steez.mapper.user.cabinet.UserCabinetMapper;
import org.website.steez.model.user.User;
import org.website.steez.model.user.UserAvatar;
import org.website.steez.security.ChangePasswordRequest;
import org.website.steez.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserAvatarMapper userAvatarMapper;
    private final UserCabinetMapper userCabinetMapper;

    @GetMapping("/cabinet")
    @PreAuthorize("hasRole('USER')")
    public UserCabinetDto getUserInfo(@AuthenticationPrincipal User principalUser) {
        User user =  userService.findById(principalUser.getId())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userCabinetMapper.toDto(user);
    }

    @PatchMapping("/change-password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, @AuthenticationPrincipal User user) {
        userService.changePassword(request, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload-avatar")
    @PreAuthorize("hasRole('USER')")
    public void uploadAvatar(@AuthenticationPrincipal User user,
                             @Validated @ModelAttribute UserAvatarDto avatarDto) {
        UserAvatar avatar = userAvatarMapper.toEntity(avatarDto);
        userService.uploadAvatar(user.getId(), avatar);
    }
}
