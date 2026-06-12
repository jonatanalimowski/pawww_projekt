package com.example.projekt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Klasa implementuje UserDetails, bo to interfejs ktory spring security rozumie
@Entity
@Data
@Table(name = "uzytkownik")
public class User implements UserDetails {

    // Generowanie indeksow w bazie
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Walidacja springowa
    @NotBlank(message = "Nazwa użytkownika jest wymagana.")
    @Size(min = 3, max = 20, message = "Nazwa musi zawierać od 3 do 20 znaków.")
    private String username;

    @NotBlank(message = "Hasło jest wymagane.")
    @Size(min = 5, message = "Hasło musi zawierać przynajmniej 3 znaki.")
    private String password;

    @NotNull(message = "Wiek jest wymagany")
    private Integer age;

    private String role;

    // Override metod ze spring security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() {return true;}

    @Override
    public boolean isAccountNonLocked() {return true;}

    @Override
    public boolean isCredentialsNonExpired() {return true;}

    @Override
    public boolean isEnabled() {return true;}
}
