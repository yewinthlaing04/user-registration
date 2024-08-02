package com.ye.registration.token;

import com.ye.registration.entity.User;

import java.util.Optional;

public interface VerificationTokenService {

    String validateToken(String token);

    void saveVerificationToken(User user , String token );

    Optional<VerificationToken> findByToken(String token );

    void deleteUserToken(Long id );
}
