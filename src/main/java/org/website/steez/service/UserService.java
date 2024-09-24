package org.website.steez.service;

import org.springframework.data.domain.Pageable;
import org.website.steez.dto.user.UserCreateEditDto;
import org.website.steez.model.user.User;
import org.website.steez.model.user.UserAvatar;
import org.website.steez.controller.request.ChangePasswordRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);

    User create(UserCreateEditDto userDto);

    List<User> findAll(Pageable pageable);

    Optional<User> findById(Long id);

    void updateAccountLockStatusById(Long id, boolean isAccountNonLock);

    void changePassword(ChangePasswordRequest request, User user);

    void uploadAvatar(Long id, UserAvatar avatar);
}
