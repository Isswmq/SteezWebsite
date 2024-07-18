package org.website.steez.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.steez.dto.UserCreateEditDto;
import org.website.steez.exception.UserNotFoundException;
import org.website.steez.model.Role;
import org.website.steez.model.User;
import org.website.steez.repository.UserRepository;
import org.website.steez.security.ChangePasswordRequest;
import org.website.steez.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User create(UserCreateEditDto userDto) {
        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(userDto.getRawPassword())
                .role(Role.USER)
                .isAccountNonLocked(true)
                .build();
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    @Override
    public Optional<User> lockOrUnlockUser(Long id, boolean isAccountNonLock) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setAccountNonLocked(false);
        userRepository.blockUserById(id, isAccountNonLock);
        return userRepository.findById(id);
    }

    @Override
    public void changePassword(ChangePasswordRequest request, User user) {

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }

        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Passwords are not the same");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
