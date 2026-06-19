package com.example.projekt.service;

import com.example.projekt.model.Category;
import com.example.projekt.model.User;
import com.example.projekt.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getCategoriesForUser(User user) {
        return categoryRepository.findByOwner(user);
    }

    public void createCategory(String name, User user) {
        if (categoryRepository.findByNameAndOwner(name, user).isPresent()) {
            throw new RuntimeException("Taka kategoria już istnieje!");
        }

        Category category = new Category();
        category.setName(name);
        category.setOwner(user);
        categoryRepository.save(category);
    }
}
