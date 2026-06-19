package com.example.projekt.controller;

import com.example.projekt.model.Category;
import com.example.projekt.model.Information;
import com.example.projekt.model.User;
import com.example.projekt.service.CategoryService;
import com.example.projekt.service.InformationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/information")
public class InformationController {

    private final InformationService informationService;
    private final CategoryService categoryService;

    public InformationController(InformationService informationService, CategoryService categoryService) {
        this.informationService = informationService;
        this.categoryService = categoryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FULL', 'LIMITED')")
    public String listInformation(Model model,
                                  @AuthenticationPrincipal User user,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String sortDir,
                                  @RequestParam(required = false) Long categoryId,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                  @CookieValue(value = "sortBy", defaultValue = "addedDate") String cookieSortBy,
                                  @CookieValue(value = "sortDir", defaultValue = "desc") String cookieSortDir,
                                  HttpServletResponse response) {

        // 1. Jeśli parametry w URL są puste, pobierz wartości z ciasteczek
        if (sortBy == null) {
            sortBy = cookieSortBy;
        }
        if (sortDir == null) {
            sortDir = cookieSortDir;
        }

        // 2. Zapisz aktualne preferencje z powrotem do ciasteczek, aby przetrwały restart przeglądarki
        Cookie sortByCookie = new Cookie("sortBy", sortBy);
        sortByCookie.setMaxAge(7 * 24 * 60 * 60); // Ciasteczko ważne przez 7 dni
        sortByCookie.setPath("/information");      // Dostępne tylko dla tej ścieżki
        response.addCookie(sortByCookie);

        Cookie sortDirCookie = new Cookie("sortDir", sortDir);
        sortDirCookie.setMaxAge(7 * 24 * 60 * 60);
        sortDirCookie.setPath("/information");
        response.addCookie(sortDirCookie);

        // 3. Przekazanie danych do serwisu i modelu (dodajemy parametry do widoku, aby szablony HTML wiedziały jak budować linki)
        model.addAttribute("information", informationService.getInformationsForUser(user, sortBy, sortDir, categoryId, date));
        model.addAttribute("sharedWithMe", informationService.getSharedWithUser(user));
        model.addAttribute("categories", categoryService.getCategoriesForUser(user));
        model.addAttribute("newInformation", new Information());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedDate", date);

        return "information/list";
    }
    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('FULL')")
    public String showAddPage() {
        return "information/add";
    }

    @PostMapping("/share-user")
    @PreAuthorize("hasRole('FULL')")
    public String shareWithUser(@RequestParam Long informationId,
                                @RequestParam String username,
                                @AuthenticationPrincipal User user) {
        informationService.shareWithUser(informationId, username, user);
        return "redirect:/information";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('FULL')")
    public String handleAddForm(@RequestParam(required = false) String action,
                                @RequestParam(required = false) String title,
                                @RequestParam(required = false) String content,
                                @RequestParam(required = false) Long categoryId,
                                @RequestParam(required = false) String newCategoryName,
                                @AuthenticationPrincipal User user,
                                Model model) {

        if ("addCategory".equals(action)) {
            if (newCategoryName != null && !newCategoryName.trim().isEmpty()) {
                try {
                    categoryService.createCategory(newCategoryName, user);
                } catch (RuntimeException e) {
                    model.addAttribute("categoryError", e.getMessage());
                }
            }

            model.addAttribute("draftTitle", title);
            model.addAttribute("draftContent", content);
            model.addAttribute("categories", categoryService.getCategoriesForUser(user));
            return "information/add";
        }

        Category category = null;
        if (categoryId != null) {
            category = new Category();
            category.setId(categoryId);
        }
        informationService.createInformation(title, content, user, category);
        return "redirect:/information";
    }
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('FULL')")
    public String editForm(@PathVariable Long id,
                           Model model,
                           @AuthenticationPrincipal User user) {
        model.addAttribute("information", informationService.getInformationById(id, user));
        model.addAttribute("categories", categoryService.getCategoriesForUser(user));
        return "information/edit"; // was informations/edit
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('FULL')")
    public String editInformation(@PathVariable Long id,
                                  @RequestParam String title,
                                  @RequestParam String content,
                                  @RequestParam(required = false) Long categoryId,
                                  @AuthenticationPrincipal User user) {
        Category category = null;
        if (categoryId != null) {
            category = new Category();
            category.setId(categoryId);
        }
        informationService.updateInformation(id, title, content, user, category);
        return "redirect:/information";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('FULL')")
    public String deleteInformation(@PathVariable Long id,
                                    @AuthenticationPrincipal User user) {
        informationService.deleteInformation(id, user);
        return "redirect:/information"; // was /informations
    }

    @GetMapping("/sharelink/{id}")
    @PreAuthorize("hasRole('FULL')")
    public String viewSharePage(@PathVariable Long id,
                                @AuthenticationPrincipal User user,
                                HttpServletRequest request,
                                Model model) {
        Information information = informationService.getInformationById(id, user);
        String link = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort()
                + "/information/shared/" + information.getShareToken();
        model.addAttribute("information", information);
        model.addAttribute("shareLink", link);
        return "information/share";
    }

    @GetMapping("/shared/{token}")
    public String viewSharedInformation(@PathVariable String token, Model model) {
        model.addAttribute("information", informationService.getInformationByToken(token));
        return "information/shared-view";
    }

}
