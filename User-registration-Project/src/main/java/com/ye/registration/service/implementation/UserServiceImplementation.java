package com.ye.registration.service.implementation;

import com.ye.registration.entity.Role;
import com.ye.registration.entity.User;
import com.ye.registration.repo.UserRepository;
import com.ye.registration.request.RegistrationRequest;
import com.ye.registration.service.UserService;
import com.ye.registration.token.VerificationTokenService;
import com.ye.registration.token.VerificationTokenServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenService verificationTokenService;
    //list all user
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(RegistrationRequest registrationRequest) {

        var user =  new User(registrationRequest.getFirstName(),
                registrationRequest.getLastName(),
                registrationRequest.getEmail(),
                passwordEncoder.encode(registrationRequest.getPassword()),  // encode password with Bcrypt
                Arrays.asList(new Role("ROLE_USER")));

        return userRepository.save(user);
    }

    // to find user with email
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow( () -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    @Override
    public void updateUser(Long id, String firstName, String lastName, String email) {
        userRepository.update(firstName , lastName , email , id );
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        Optional<User> deleteUser = userRepository.findById(id);
        deleteUser.ifPresent( user -> verificationTokenService.deleteUserToken(user.getId()));
        userRepository.deleteById(id);
    }
}
