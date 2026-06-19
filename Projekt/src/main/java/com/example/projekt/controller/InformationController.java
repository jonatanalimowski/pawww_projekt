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
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

        if (sortBy == null) {
            sortBy = cookieSortBy;
        }
        if (sortDir == null) {
            sortDir = cookieSortDir;
        }

        Cookie sortByCookie = new Cookie("sortBy", sortBy);
        sortByCookie.setMaxAge(7 * 24 * 60 * 60);
        sortByCookie.setPath("/information");
        response.addCookie(sortByCookie);

        Cookie sortDirCookie = new Cookie("sortDir", sortDir);
        sortDirCookie.setMaxAge(7 * 24 * 60 * 60);
        sortDirCookie.setPath("/information");
        response.addCookie(sortDirCookie);

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
    public String showAddPage(Model model, @AuthenticationPrincipal User user) {

        model.addAttribute("category", new Category());
        model.addAttribute("information", new Information());
        model.addAttribute("categories", categoryService.getCategoriesForUser(user));
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
    public String handleAddForm(
            @Valid @ModelAttribute("information") Information information,
            BindingResult result,
            @RequestParam(required = false) Long categoryId,
            @AuthenticationPrincipal User user,
            Model model) {

        Category category = null;
        if (categoryId != null) {
            category = new Category();
            category.setId(categoryId);
        }

        informationService.createInformation(
                information.getTitle(),
                information.getContent(),
                user,
                category
        );

        return "redirect:/information";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('FULL')")
    public String editForm(@PathVariable Long id,
                           Model model,
                           @AuthenticationPrincipal User user) {
        model.addAttribute("information", informationService.getInformationById(id, user));
        model.addAttribute("categories", categoryService.getCategoriesForUser(user));
        return "information/edit";
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
        return "redirect:/information";
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
