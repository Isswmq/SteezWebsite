package org.website.steez.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.steez.dto.UserCreateEditDto;
import org.website.steez.exception.UserNotFoundException;
import org.website.steez.model.user.Role;
import org.website.steez.model.user.User;
import org.website.steez.model.user.UserAvatar;
import org.website.steez.repository.UserRepository;
import org.website.steez.security.ChangePasswordRequest;
import org.website.steez.service.UserAvatarService;
import org.website.steez.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAvatarService userAvatarService;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "UserService::findById", key = "#id")
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "UserService::findByEmail", key = "#email")
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional
    @Caching(put = {
            @CachePut(value = "UserService::findById", key = "#result.id"),
            @CachePut(value = "UserService::findByEmail", key = "#result.email")
    })
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
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "UserService::findById", key = "#id"),
            @CacheEvict(value = "UserService::findByEmail", key = "#result.email")
    },
            put =  @CachePut(value = "UserService::findById", key = "#id"))
    public void updateAccountLockStatusById(Long id, boolean isAccountNonLock) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with ID " + id + " not found");
        }
        userRepository.updateAccountLockStatusById(id, isAccountNonLock);
    }

    @Override
    @Transactional
    @Caching(put = {
            @CachePut(value = "UserService::findById", key = "#user.id"),
            @CachePut(value = "UserService::findByEmail", key = "#user.email")
    })
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

    @Override
    @Transactional
    @CacheEvict(value = "UserService::findById", key = "#id")
    public void uploadAvatar(Long id, UserAvatar avatar) {
         User user = findById(id).orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
         String fileName = userAvatarService.upload(avatar);
         user.setAvatar(fileName);
         userRepository.save(user);
    }
}
