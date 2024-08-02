package com.ye.registration.passwordReset;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetRepo extends JpaRepository< PasswordResetToken , Long> {


    Optional<PasswordResetToken> findByToken(String theToken);
}
