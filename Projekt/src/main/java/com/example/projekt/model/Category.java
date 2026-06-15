package com.example.projekt.model;

import lombok.Data;

// Klasa trzymajaca kategorie notatki, pozniej bedzie weryfikacja za pomoca RESTa
@Data
public class Category {
    private Long id;
    private String name;
}
