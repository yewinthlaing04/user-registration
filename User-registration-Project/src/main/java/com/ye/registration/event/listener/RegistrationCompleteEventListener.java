package com.ye.registration.event.listener;


import com.ye.registration.entity.User;
import com.ye.registration.event.RegistrationCompleteEvent;
import com.ye.registration.token.VerificationTokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final VerificationTokenService verificationTokenService;

    private final JavaMailSender mailSender;

    private User user;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // get the user
        user = event.getUser();

        // generate a token for user
        String token = UUID.randomUUID().toString();

        // save token for the user
        verificationTokenService.saveVerificationToken(user , token);

        // build verification url
        String url = event.getConfirmationUrl()+"/registration/verifyEmail?token="+token;

        // send the email to the user
        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendVerificationEmail(String url)throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "Users Verification Service";
        String mailContent = "<p>Hi, "+user.getFirstName()+", </p>"+
                "<p>Thank you for registering with Salarix Tech,"+""+
                "Please follow the link below to verify your registration.</p>"+
                "<a href=\""+url+"\">Verify your email to activate your account</a>"+
                "<p>Thank you <br> Users Registration Portal Service.</p>";

        emailMessage(subject , senderName , mailContent , mailSender , user);

    }

    public void sendPasswordResetEmail(String url)throws MessagingException, UnsupportedEncodingException {
        String subject = "Password Reset Email";
        String senderName = "Users Password Reset Service";
        String mailContent = "<p>Hi, "+user.getFirstName()+", </p>"+
                "<p>You recently request to reset your password,"+""+
                "Please follow the link below to reset your password.</p>"+
                "<a href=\""+url+"\">Verify your email to reset your password</a>"+
                "<p>Thank you <br> Users Password Reset Portal Service.</p>";

        emailMessage(subject , senderName , mailContent , mailSender , user);

    }

    private static void emailMessage(String subject, String senderName,
                                     String mailContent, JavaMailSender mailSender, User theUser)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("youremail", senderName);
        messageHelper.setTo(theUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
