package com.ye.registration.securityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)  // disable csrf
                .authorizeHttpRequests( req -> req.requestMatchers("/",
                                "/login",
                                "/error",
                                "/registration/**").permitAll()
                        .anyRequest().authenticated())  // request authenticate
                .formLogin( form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout( logout -> logout
                        .invalidateHttpSession(true)  // invalid http session when user logs out
                        .clearAuthentication(true)  // clears authentication information when user logs out
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // specifies url pattern to trigger a logout
                        .logoutSuccessUrl("/"));  //defines url to redirect to after a successful logout

               return http.build();
    }
}
