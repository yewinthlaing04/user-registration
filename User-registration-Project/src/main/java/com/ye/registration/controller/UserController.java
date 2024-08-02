package com.ye.registration.controller;

import com.ye.registration.entity.User;
import com.ye.registration.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // methods to get all users
    @GetMapping
    public String getAllUser(ModelMap modelMap){

        modelMap.addAttribute("users", userService.getAllUsers());

        return "users";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id , ModelMap modelMap){

        Optional<User> user = userService.findById(id);
        modelMap.addAttribute("user", user.get());

        return "update-user";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id , User user ){

        userService.updateUser( id , user.getFirstName() , user.getLastName() , user.getEmail());

        return "redirect:/users?update_success";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id ) {
        userService.deleteUser(id);
        return "redirect:/users?delete_success";
    }
}
