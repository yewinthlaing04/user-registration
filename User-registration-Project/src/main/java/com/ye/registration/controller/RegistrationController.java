package com.ye.registration.controller;


import com.ye.registration.entity.User;
import com.ye.registration.event.RegistrationCompleteEvent;
import com.ye.registration.event.listener.RegistrationCompleteEventListener;
import com.ye.registration.passwordReset.PasswordResetService;
import com.ye.registration.request.RegistrationRequest;
import com.ye.registration.service.UserService;
import com.ye.registration.token.VerificationToken;
import com.ye.registration.token.VerificationTokenService;
import com.ye.registration.utility.UrlUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {

    private final UserService userService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final VerificationTokenService verificationTokenService;

    private final PasswordResetService passwordResetService;

    private final RegistrationCompleteEventListener registrationCompleteEventListener;

    @GetMapping("/registration-form")
    public String showRegistrationForm(ModelMap modelMap) {

        modelMap.addAttribute("user" , new RegistrationRequest());

        return "registration";
    }


    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user")
                               RegistrationRequest registrationRequest  ,
                               HttpServletRequest request ) {

        User user = userService.registerUser(registrationRequest);

        // publish the verification email event
        applicationEventPublisher.publishEvent(
                new RegistrationCompleteEvent(user , UrlUtil.getApplicationUrl(request)));

        return "redirect:/registration/registration-form?success";
    }


    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token) {
        Optional<VerificationToken> theToken = verificationTokenService.findByToken(token);
        if (theToken.isPresent() && theToken.get().getUser().isEnabled()) {
            return "redirect:/login?verified";
        }
        String verificationResult = verificationTokenService.validateToken(token);

        switch (verificationResult.toLowerCase()) {
            case "expired":
                return "redirect:/error?expired";
            case "valid":
                return "redirect:/login?valid";
            default:
                return "redirect:/error?invalid";
        }
    }


    @GetMapping("/forgot-password")
    public String forgotPasswordForm(){
        return "forgot-password";
    }

    @PostMapping("/reset-password-email")
    public String resetPasswordRequest(HttpServletRequest request , ModelMap modelMap){

       String email = request.getParameter("email");
       User user = userService.findByEmail(email);

       if ( user == null ) {
           return "redirect:/registration/forgot-password?not_found";
       }

       String passwordResetToken = UUID.randomUUID().toString();

       passwordResetService.createPasswordResetTokenForUser(user , passwordResetToken );

       // send password reset verification to the user
        String url = UrlUtil.getApplicationUrl(request)+"/registration/reset-form?token="+passwordResetToken;

        try {
            registrationCompleteEventListener.sendPasswordResetEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            modelMap.addAttribute( "error" , e.getMessage());
        }

        return "redirect:/registration/forgot-password?success";
    }

    @GetMapping("/reset-form")
    public String passwordResetForm(@RequestParam("token") String token , ModelMap modelMap){
        modelMap.addAttribute("token" , token );
        return "password-reset-form";
    }

    @PostMapping("/reset-password")
    public String resetPassword(HttpServletRequest request){

        String theToken = request.getParameter("token");
        String password = request.getParameter("password");

        String tokenVerificationResult = passwordResetService.validatePasswordResetToken(theToken);

        if ( !tokenVerificationResult.equalsIgnoreCase("valid")){
            return "redirect:/error?invalid_token";
        }

        Optional<User> theUser = passwordResetService.findUserByPasswordResetToken(theToken);

        if ( theUser.isPresent()){
            passwordResetService.resetPassword(theUser.get() , password);
            return "redirect:/login?reset_success";
        }

        return "redirect:/error?not_found";
    }


}
