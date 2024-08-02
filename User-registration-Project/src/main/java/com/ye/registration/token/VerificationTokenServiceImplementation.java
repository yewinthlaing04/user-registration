package com.ye.registration.token;

import com.ye.registration.entity.User;
import com.ye.registration.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImplementation implements VerificationTokenService {

    private final UserRepository userRepository;

    private final VerificationTokenRepo verificationTokenRepo;

    @Override
    public String validateToken(String token) {

        Optional<VerificationToken> verificationToken = verificationTokenRepo.findByToken(token);

        if (verificationToken.isEmpty()){
            return "INVALID";
        }

        User user = verificationToken.get().getUser();

        Calendar calendar = Calendar.getInstance();

        if ( verificationToken.get().getExpirationTime().getTime()
                - calendar.getTime().getTime() <= 0 ){
            return "EXPIRED";
        }

        user.setEnabled(true);
        userRepository.save(user);

        return "VALID";

    }

    @Override
    public void saveVerificationToken(User user, String token) {

        var verificationToken = new VerificationToken(token , user);

        verificationTokenRepo.save(verificationToken);



    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return verificationTokenRepo.findByToken(token);
    }

    @Override
    public void deleteUserToken(Long id){
        verificationTokenRepo.deleteByUserId(id);
    }
}
