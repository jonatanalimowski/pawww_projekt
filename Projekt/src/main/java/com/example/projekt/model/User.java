package com.example.projekt.model;

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
//@Entity
//@Data
//@Table(name = "uzytkownik")
//public class User implements UserDetails {
//
//    // Generowanie indeksow w bazie
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // Walidacja springowa
//    @NotBlank(message = "Nazwa użytkownika jest wymagana.")
//    @Size(min = 3, max = 20, message = "Nazwa musi zawierać od 3 do 20 znaków.")
//    private String username;
//
//    @NotBlank(message = "Hasło jest wymagane.")
//    @Size(min = 5, message = "Hasło musi zawierać przynajmniej 5 znaki.")
//    private String password;
//
//    @NotNull(message = "Wiek jest wymagany")
//    private Integer age;
//
//    @Enumerated(EnumType.STRING)
//    private Role role;
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(role.name()));
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {return true;}
//
//    @Override
//    public boolean isAccountNonLocked() {return true;}
//
//    @Override
//    public boolean isCredentialsNonExpired() {return true;}
//
//    @Override
//    public boolean isEnabled() {return true;}
//
//    @Override public String getUsername() { return username; }
//
//    @Override public String getPassword() { return password; }
//}

@Entity
@Table(name = "uzytkownik")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nazwa użytkownika jest wymagana.")
    @Size(min = 3, max = 20, message = "Nazwa musi zawierać od 3 do 20 znaków.")
    private String username;

    @NotBlank(message = "Hasło jest wymagane.")
    @Size(min = 5, message = "Hasło musi zawierać przynajmniej 5 znaki.")
    private String password;

    @NotNull(message = "Wiek jest wymagany")
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Role role;

    // manual getters and setters instead of @Data
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setAge(Integer age) { this.age = age; }
    public Integer getAge() { return age; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    @Override
    public String getUsername() { return username; }

    @Override
    public String getPassword() { return password; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
