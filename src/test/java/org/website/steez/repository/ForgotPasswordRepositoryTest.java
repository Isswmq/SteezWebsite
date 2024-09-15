package org.website.steez.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.website.steez.model.ForgotPassword;
import org.website.steez.model.user.Role;
import org.website.steez.model.user.User;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;
import java.util.Optional;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ForgotPasswordRepositoryTest {

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void ForgotPasswordRepository_findByOtpAndUser_ReturnOptionalForgotPassword() {
        User user = User.builder()
                .id(1L)
                .username("steez")
                .email("steezhack@gmail.com")
                .role(Role.USER)
                .isAccountNonLocked(true)
                .build();

        userRepository.save(user);
        int otp = 999_999;

        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .user(user)
                .build();

        forgotPasswordRepository.save(forgotPassword);

        Optional<ForgotPassword> maybeForgotPassword = forgotPasswordRepository.findByOtpAndUser(otp, user);
        assertThat(maybeForgotPassword).isPresent();
        assertThat(maybeForgotPassword.get().getOtp()).isEqualTo(otp);
        assertThat(maybeForgotPassword.get().getUser()).isEqualTo(user);
        assertThat(maybeForgotPassword.get().getExpirationTime()).isEqualTo(forgotPassword.getExpirationTime());
    }

    @Test
    @DirtiesContext
    public void ForgotPasswordRepository_findByOtpAndUser_ReturnsEmptyForInvalidOtp() {
        User user = User.builder()
                .id(1L)
                .username("steez")
                .email("steezhack@gmail.com")
                .role(Role.USER)
                .isAccountNonLocked(true)
                .build();

        userRepository.save(user);
        int validOtp = 999_999;
        int invalidOtp = 123456;

        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(validOtp)
                .expirationTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .user(user)
                .build();

        forgotPasswordRepository.save(forgotPassword);

        Optional<ForgotPassword> maybeForgotPassword = forgotPasswordRepository.findByOtpAndUser(invalidOtp, user);
        assertThat(maybeForgotPassword).isNotPresent();
    }

    @Test
    @DirtiesContext
    public void ForgotPasswordRepository_findByOtpAndUser_ReturnsEmptyForInvalidUser() {
        User user = User.builder()
                .id(1L)
                .username("steez")
                .email("steezhack@gmail.com")
                .role(Role.USER)
                .isAccountNonLocked(true)
                .build();

        User invalidUser = User.builder()
                .id(2L)
                .username("invalidUser")
                .email("invalid@example.com")
                .role(Role.USER)
                .isAccountNonLocked(true)
                .build();

        userRepository.save(user);
        int otp = 999_999;

        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .user(user)
                .build();

        forgotPasswordRepository.save(forgotPassword);

        Optional<ForgotPassword> maybeForgotPassword = forgotPasswordRepository.findByOtpAndUser(otp, invalidUser);
        assertThat(maybeForgotPassword).isNotPresent();
    }

    @Test
    @DirtiesContext
    public void ForgotPasswordRepository_findByOtpAndUser_ReturnsCorrectRecordWhenMultipleRecordsExist() {
        User user = User.builder()
                .id(1L)
                .username("steez")
                .email("steezhack@gmail.com")
                .role(Role.USER)
                .isAccountNonLocked(true)
                .build();

        User anotherUser = User.builder()
                .id(2L)
                .username("anotherUser")
                .email("another@example.com")
                .role(Role.USER)
                .isAccountNonLocked(true)
                .build();

        userRepository.save(user);
        userRepository.save(anotherUser);

        int otpForUser = 999_999;
        int otpForAnotherUser = 888_888;

        ForgotPassword forgotPasswordForUser = ForgotPassword.builder()
                .otp(otpForUser)
                .expirationTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .user(user)
                .build();

        ForgotPassword forgotPasswordForAnotherUser = ForgotPassword.builder()
                .otp(otpForAnotherUser)
                .expirationTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .user(anotherUser)
                .build();

        forgotPasswordRepository.save(forgotPasswordForUser);
        forgotPasswordRepository.save(forgotPasswordForAnotherUser);

        Optional<ForgotPassword> maybeForgotPassword = forgotPasswordRepository.findByOtpAndUser(otpForUser, user);
        assertThat(maybeForgotPassword).isPresent();
        assertThat(maybeForgotPassword.get().getOtp()).isEqualTo(otpForUser);
        assertThat(maybeForgotPassword.get().getUser()).isEqualTo(user);

        Optional<ForgotPassword> maybeForgotPasswordForAnotherUser = forgotPasswordRepository.findByOtpAndUser(otpForAnotherUser, anotherUser);
        assertThat(maybeForgotPasswordForAnotherUser).isPresent();
        assertThat(maybeForgotPasswordForAnotherUser.get().getOtp()).isEqualTo(otpForAnotherUser);
        assertThat(maybeForgotPasswordForAnotherUser.get().getUser()).isEqualTo(anotherUser);
    }
}
