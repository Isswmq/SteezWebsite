package org.website.steez.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.website.steez.model.User;
import org.website.steez.repository.UserRepository;
import org.website.steez.service.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
