package com.ye.registration.passwordReset;

import com.ye.registration.entity.User;
import com.ye.registration.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImplementation implements  PasswordResetService {

    private final PasswordResetRepo passwordResetRepo;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public String validatePasswordResetToken(String theToken) {

        Optional<PasswordResetToken> passwordResetToken = passwordResetRepo.findByToken(theToken);

        if ( passwordResetToken.isEmpty()){
            return "invalid";
        }

        Calendar calendar = Calendar.getInstance();

        if( passwordResetToken.get().getExpirationTime().getTime() -
            calendar.getTime().getTime() <= 0
        ){
            return "expired";
        }


        return "valid";
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String passwordResetToken) {

        PasswordResetToken resetToken = new PasswordResetToken(passwordResetToken , user);

        passwordResetRepo.save(resetToken);
    }

    @Override
    public Optional<User> findUserByPasswordResetToken(String theToken) {
        return Optional.ofNullable(passwordResetRepo.findByToken(theToken).get().getUser());
    }

    @Override
    public void resetPassword(User theUser, String newPassword) {
        theUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(theUser);
    }
}
