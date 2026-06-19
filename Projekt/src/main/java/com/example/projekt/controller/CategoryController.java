package com.example.projekt.controller;

import com.example.projekt.model.Category;
import com.example.projekt.model.User;
import com.example.projekt.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public String createCategory(@Valid @ModelAttribute("category") Category category,
                                 BindingResult result,
                                 @AuthenticationPrincipal User user,
                                 Model model) {

        if (result.hasErrors()) {
            model.addAttribute("category", category);
            return "redirect:/information/add";
        }

        categoryService.createCategory(category.getName(), user);
        return "redirect:/information/add";
    }
}