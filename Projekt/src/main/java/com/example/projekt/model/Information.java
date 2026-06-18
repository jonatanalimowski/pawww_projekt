package com.example.projekt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "information")
@NoArgsConstructor
@Setter
@Getter
public class Information {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tytuł jest wymagany")
    private String title;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Treść jest wymagana")
    private String content;

    private LocalDate addedDate = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(unique = true)
    private String shareToken;
}
