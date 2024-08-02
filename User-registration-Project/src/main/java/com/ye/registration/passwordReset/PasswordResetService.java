package com.ye.registration.passwordReset;

import com.ye.registration.entity.User;

import java.util.Optional;

public interface PasswordResetService {

    String validatePasswordResetToken(String theToken);

    void createPasswordResetTokenForUser(User user, String passwordResetToken);

    Optional<User> findUserByPasswordResetToken(String theToken);

    void resetPassword(User theUser, String password);
}
