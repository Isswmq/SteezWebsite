package org.website.steez.service;

import org.website.steez.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);
}
