package com.example.projekt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

//Klasa trzymajaca notatke
@NoArgsConstructor
@Setter
@Getter
public class Information {
    private Long id;
    private String title;
    private String content;
    private LocalDate addedDate = LocalDate.now();
    private Category category;
    private User owner;
}
