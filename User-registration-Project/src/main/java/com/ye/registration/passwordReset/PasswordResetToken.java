package com.ye.registration.passwordReset;

import com.ye.registration.entity.User;
import com.ye.registration.token.TokenExpirationTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date expirationTime;
    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    public PasswordResetToken(String token , User user) {
        this.token = token;
        this.expirationTime = TokenExpirationTime.getExpirationTime();
        this.user = user;
    }

}
