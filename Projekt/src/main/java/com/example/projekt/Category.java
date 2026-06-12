package com.example.projekt;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Klasa trzymajaca kategorie notatki, pozniej bedzie weryfikacja za pomoca RESTa
@Data
public class Category {
    private Long id;
    private String name;
}
