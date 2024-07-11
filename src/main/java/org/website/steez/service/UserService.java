package org.website.steez.service;

import org.springframework.data.domain.Pageable;
import org.website.steez.dto.UserCreateEditDto;
import org.website.steez.model.User;
import org.website.steez.security.ChangePasswordRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);

    User create(UserCreateEditDto userDto);

    List<User> findAll(Pageable pageable);

    Optional<User> findById(Long id);

    Optional<User> lockOrUnlockUser(Long id, boolean isAccountNonLock);

    void changePassword(ChangePasswordRequest request, User user);
}
