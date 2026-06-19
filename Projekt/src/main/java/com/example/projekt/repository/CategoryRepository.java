package com.example.projekt.repository;

import com.example.projekt.model.Category;
import com.example.projekt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByOwner(User owner);
    Optional<Category> findByNameAndOwner(String name, User owner);
}