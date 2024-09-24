package org.website.steez.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.website.steez.dto.user.UserAvatarDto;
import org.website.steez.dto.user.UserCabinetDto;
import org.website.steez.mapper.user.avatar.UserAvatarMapper;
import org.website.steez.mapper.user.cabinet.UserCabinetMapper;
import org.website.steez.model.user.User;
import org.website.steez.model.user.UserAvatar;
import org.website.steez.controller.request.ChangePasswordRequest;
import org.website.steez.service.UserService;

import java.util.Arrays;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User Controller", description = "User API")
public class UserController {

    private final UserService userService;
    private final UserAvatarMapper userAvatarMapper;
    private final UserCabinetMapper userCabinetMapper;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final String[] ALLOWED_CONTENT_TYPES = {"image/jpeg", "image/png", "image/gif"};

    @GetMapping("/cabinet")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public UserCabinetDto getUserInfo(@AuthenticationPrincipal User principalUser) {
        User user =  userService.findById(principalUser.getId())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userCabinetMapper.toDto(user);
    }

    @PatchMapping("/change-password")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> changePassword(@Validated @RequestBody ChangePasswordRequest request, @AuthenticationPrincipal User user) {
        userService.changePassword(request, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload-avatar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> uploadAvatar(@AuthenticationPrincipal User user,
                             @Validated @ModelAttribute UserAvatarDto avatarDto) {
        MultipartFile file = avatarDto.getFile();

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File must not be empty.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body("File size exceeds the maximum limit of 5MB.");
        }

        if (!Arrays.asList(ALLOWED_CONTENT_TYPES).contains(file.getContentType())) {
            return ResponseEntity.badRequest().body("Unsupported file type.");
        }

        UserAvatar avatar = userAvatarMapper.toEntity(avatarDto);
        userService.uploadAvatar(user.getId(), avatar);

        return ResponseEntity.ok("Avatar upload successfully");
    }
}
