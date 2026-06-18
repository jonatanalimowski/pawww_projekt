package com.example.projekt.controller;

import com.example.projekt.model.User;
import com.example.projekt.service.CategoryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create")
    public String createCategory(@RequestParam String name, @AuthenticationPrincipal User user) {
        categoryService.createCategory(name, user);
        return "redirect:/information";
    }
}
