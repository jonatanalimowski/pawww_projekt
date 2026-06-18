package com.example.projekt.controller;

import com.example.projekt.model.Category;
import com.example.projekt.model.Information;
import com.example.projekt.model.User;
import com.example.projekt.service.CategoryService;
import com.example.projekt.service.InformationService;
import jakarta.servlet.http.HttpServletRequest;
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
                                  HttpSession session) {

        if (sortBy == null) {
            sortBy = (String) session.getAttribute("sortBy");
        }
        if (sortDir == null) {
            sortDir = (String) session.getAttribute("sortDir");
        }


        if (sortBy == null) sortBy = "addedDate";
        if (sortDir == null) sortDir = "desc";

        session.setAttribute("sortBy", sortBy);
        session.setAttribute("sortDir", sortDir);

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
    public String createInformation(@RequestParam String title,
                                    @RequestParam String content,
                                    @RequestParam(required = false) Long categoryId,
                                    @AuthenticationPrincipal User user) {
        Category category = null;
        if (categoryId != null) {
            // Simple approach: we assume the user owns the category or we could verify it
            // For now, let's just use it
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
