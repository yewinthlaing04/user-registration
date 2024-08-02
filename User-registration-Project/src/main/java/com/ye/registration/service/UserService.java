package com.ye.registration.service;

import com.ye.registration.entity.User;
import com.ye.registration.request.RegistrationRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();

    User registerUser(RegistrationRequest registrationRequest);

    User findByEmail(String email);

    Optional<User> findById(Long id);

    void updateUser(Long id, String firstName, String lastName, String email);

    void deleteUser(Long id);
}
