package org.website.steez.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.website.steez.model.user.Role;
import org.website.steez.model.user.User;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DirtiesContext
    public void UserRepository_findById_ReturnOptionalUser() {
        User user = User.builder()
                .id(1L)
                .username("steez")
                .email("steezhack@gmail.com")
                .role(Role.USER)
                .isAccountNonLocked(true)
                .build();

        userRepository.save(user);
        Optional<User> optionalUser = userRepository.findById(user.getId());

        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get().getId()).isEqualTo(user.getId());
    }

    @Test
    @DirtiesContext
    public void UserRepository_findById_ReturnEmptyOptional() {
        Long userId = 1L;

        Optional<User> optionalUser = userRepository.findById(userId);
        assertThat(optionalUser).isNotPresent();
    }

    @Test
    @DirtiesContext
    public void UserRepository_findAll_ReturnMoreThenOneUser() {
        User user = User.builder().id(1L).username("steez").isAccountNonLocked(true)
                .email("steezhack1@gmail.com").role(Role.USER)
                .build();
        User user2 = User.builder().id(2L).username("steez2").isAccountNonLocked(true)
                .email("steezhack2@gmail.com").role(Role.USER)
                .build();

        userRepository.save(user);
        userRepository.save(user2);

        List<User> userList = userRepository.findAll();
        assertThat(userList).isNotNull();
        assertThat(userList.size()).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    public void UserRepository_findAll_ReturnEmptyList() {
        List<User> userList = userRepository.findAll();
        assertThat(userList).isEmpty();
    }


    @Test
    @DirtiesContext
    public void UserRepository_findByEmail_ReturnOptionalUser() {
        User user = User.builder().id(1L).username("steezhack").isAccountNonLocked(true)
                .email("steezhack1@gmail.com").role(Role.USER)
                .build();

        userRepository.save(user);
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());

        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DirtiesContext
    public void UserRepository_findByEmail_ReturnEmptyOptional() {
        String email = "steez@gmail.com";
        Optional<User> optionalUser = userRepository.findByEmail(email);

        assertThat(optionalUser).isNotPresent();
    }

    @Test
    @DirtiesContext
    public void UserRepository_updateAccountLockStatusById_ReturnBlockedUser() {
        User user = User.builder().id(1L).email("steez@gmail.com").isAccountNonLocked(true)
                .username("steezhack1").role(Role.USER)
                .build();
        userRepository.save(user);

        userRepository.updateAccountLockStatusById(user.getId(), false);

        entityManager.flush();
        entityManager.clear();

        User updatedUser = userRepository.findById(user.getId()).get();

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.isAccountNonLocked()).isFalse();
    }

    @Test
    @DirtiesContext
    public void UserRepository_updateAccountLockStatusById_ReturnUnblockUser() {
        User user = User.builder().id(1L).email("steez@gmail.com").isAccountNonLocked(false)
                .username("steezhack1").role(Role.USER)
                .build();
        userRepository.save(user);

        userRepository.updateAccountLockStatusById(user.getId(), true);

        entityManager.flush();
        entityManager.clear();

        User updatedUser = userRepository.findById(user.getId()).get();

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.isAccountNonLocked()).isTrue();
    }

    @Test
    @DirtiesContext
    public void UserRepository_updatePassword_ReturnUserWithNewPassword() {
        User user = User.builder().id(1L).email("steez@gmail.com").isAccountNonLocked(true)
                .username("steezhack").role(Role.USER)
                .password("weakPassword1337")
                .build();

        String newPassword = "strongPassword1337";
        userRepository.save(user);
        userRepository.updatePassword(user.getEmail(), newPassword);

        entityManager.flush();
        entityManager.clear();

        User updatedUser = userRepository.findById(user.getId()).get();
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getPassword()).isEqualTo(newPassword);
    }
}
