package org.website.steez.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.website.steez.model.ForgotPassword;
import org.website.steez.model.user.User;

import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {

    @Query("SELECT fp FROM  ForgotPassword fp WHERE fp.otp = :otp and fp.user = :user")
    Optional<ForgotPassword> findByOtpAndUser(@Param("otp") Integer otp, @Param("user") User user);

}
