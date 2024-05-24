package org.website.steez.service;

import org.website.steez.dto.UserCreateEditDto;
import org.website.steez.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);

    User create(UserCreateEditDto userDto);
}
