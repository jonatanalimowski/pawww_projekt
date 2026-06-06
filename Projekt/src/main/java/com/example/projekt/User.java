package com.example.projekt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class User {
    private Long id;
    private String username;
    private String password;
    private Integer age;
    private String role;
}
