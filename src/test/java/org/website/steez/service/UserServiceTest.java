package org.website.steez.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.website.steez.controller.request.ChangePasswordRequest;
import org.website.steez.dto.user.UserCreateEditDto;
import org.website.steez.exception.UserNotFoundException;
import org.website.steez.model.user.Role;
import org.website.steez.model.user.User;
import org.website.steez.model.user.UserAvatar;
import org.website.steez.repository.UserRepository;
import org.website.steez.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAvatarService userAvatarService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void UserService_create_ReturnSavedUser() {
        User user = User.builder().id(1L).email("steez@gmail.com").isAccountNonLocked(false)
                .username("steezhack1").role(Role.USER)
                .build();

        UserCreateEditDto dto = UserCreateEditDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .build();

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        User savedUser = userService.create(dto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getId()).isEqualTo(user.getId());
    }

    @Test
    public void findById_WhenUserExists_ReturnsUser() {
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@gmail.com").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findById(userId);

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(user);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void create_WhenValidDto_CreatesAndReturnsUser() {
        UserCreateEditDto dto = UserCreateEditDto.builder()
                .username("steez")
                .email("steez@gmail.com")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .username(dto.getUsername())
                .email(dto.getEmail())
                .role(Role.USER)
                .isAccountNonLocked(true)
                .build();

        when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        User result = userService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo(dto.getEmail());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void updateAccountLockStatusById_WhenUserExists_UpdatesLockStatus() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.updateAccountLockStatusById(userId, true);

        verify(userRepository, times(1)).updateAccountLockStatusById(userId, true);
    }

    @Test
    public void updateAccountLockStatusById_WhenUserDoesNotExist_ThrowsException() {

        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.updateAccountLockStatusById(userId, true));
        verify(userRepository, times(0)).updateAccountLockStatusById(anyLong(), anyBoolean());
    }

    @Test
    public void changePassword_WhenCurrentPasswordIsCorrect_ChangesPassword() {

        ChangePasswordRequest request = new ChangePasswordRequest("currentPass", "newPass", "newPass");
        User user = User.builder().id(1L).password("encodedCurrentPass").build();
        when(passwordEncoder.matches("currentPass", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        userService.changePassword(request, user);

        assertThat(user.getPassword()).isEqualTo("encodedNewPass");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void changePassword_WhenCurrentPasswordIsIncorrect_ThrowsException() {

        ChangePasswordRequest request = new ChangePasswordRequest("wrongCurrentPass", "newPass", "newPass");
        User user = User.builder().id(1L).password("encodedCurrentPass").build();
        when(passwordEncoder.matches("wrongCurrentPass", user.getPassword())).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> userService.changePassword(request, user));
        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void changePassword_WhenNewPasswordDoesNotMatch_ThrowsException() {
        ChangePasswordRequest request = new ChangePasswordRequest("currentPass", "newPass", "mismatchPass");
        User user = User.builder().id(1L).password("encodedCurrentPass").build();
        when(passwordEncoder.matches("currentPass", user.getPassword())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> userService.changePassword(request, user));
        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void uploadAvatar_WhenUserExists_UploadsAvatar() {
        Long userId = 1L;
        String uploadedAvatarFileName = "avatar.png";
        User user = User.builder().id(userId).build();

        MultipartFile multipartFile = mock(MultipartFile.class);
        UserAvatar userAvatar = UserAvatar.builder()
                .file(multipartFile)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userAvatarService.upload(userAvatar)).thenReturn(uploadedAvatarFileName);

        userService.uploadAvatar(userId, userAvatar);

        assertThat(user.getAvatar()).isEqualTo(uploadedAvatarFileName);
        verify(userRepository, times(1)).save(user);
    }



    @Test
    public void uploadAvatar_WhenUserDoesNotExist_ThrowsException() {
        Long userId = 1L;
        MultipartFile multipartFile = mock(MultipartFile.class);
        UserAvatar avatar = UserAvatar.builder()
                .file(multipartFile)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.uploadAvatar(userId, avatar));
        verify(userRepository, times(0)).save(any(User.class));
    }
}
