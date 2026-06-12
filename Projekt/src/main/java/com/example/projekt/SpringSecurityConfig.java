package com.example.projekt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.AntPathMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    // ustawienie sposobu szyfrowania hasel
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Zeby spring security nie blokowal dostepu do bazy (myslalem ze to pomoze, ale nie pomoglo, dalej nie mozne obejrzec konsoli)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/h2-console/**")
                .requestMatchers("/h2-console");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/css/**", "/js/**").permitAll() // ogolnodostepne
                        .requestMatchers("/h2-console/**").permitAll() // dostęp do bazy
                        .requestMatchers("/h2-console").permitAll() // dostep do bazy
                        .anyRequest().authenticated() // reszta wymaga zalogowania
                )
                .formLogin(form -> form
                        .loginPage("/login") // widok logowania
                        .defaultSuccessUrl("/home") // tutaj przekierowuje po zalogowaniu
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

}
