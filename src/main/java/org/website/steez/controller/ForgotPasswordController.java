package org.website.steez.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.website.steez.dto.EmailDto;
import org.website.steez.exception.UserNotFoundException;
import org.website.steez.model.ForgotPassword;
import org.website.steez.model.user.User;
import org.website.steez.repository.ForgotPasswordRepository;
import org.website.steez.repository.UserRepository;
import org.website.steez.security.ChangeForgotPasswordRequest;
import org.website.steez.service.EmailService;

import java.time.Instant;
import java.util.Date;
import java.util.Random;

@Tag(name = "Forgot Password Controller",description = "Forgot Password API")
@RestController
@RequestMapping("/api/v1/forgotPassword")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder encoder;

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UserNotFoundException("Please provide a valid email"));

        int otp = generateOtp();
        EmailDto emailDto = EmailDto.builder()
                .to(email)
                .text("This is the OTP for your Forgot Password request : " + otp)
                .subject("OTP for Forgot Password request")
                .build();

        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .user(user)
                .build();

        emailService.sendSimpleMessage(emailDto);
        forgotPasswordRepository.save(forgotPassword);

        return ResponseEntity.ok("Email send for verification");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UserNotFoundException("Please provide a valid email"));


        ForgotPassword forgotPassword = forgotPasswordRepository.findByOtpAndUserAnd(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + email));

        if (forgotPassword.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.delete(forgotPassword);
            return new ResponseEntity<>("OTP has expired", HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("OTP verified");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePassword(@RequestBody ChangeForgotPasswordRequest request,
                                                 @PathVariable String email) {
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            return new ResponseEntity<>("Passwords are not the same", HttpStatus.EXPECTATION_FAILED);
        }

        String encodePassword = encoder.encode(request.getNewPassword());
        userRepository.updatePassword(email, encodePassword);
        return ResponseEntity.ok("Password has been changed");
    }

    private Integer generateOtp() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
