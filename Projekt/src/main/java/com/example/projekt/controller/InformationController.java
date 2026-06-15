package com.example.projekt.controller;

import com.example.projekt.model.Information;
import com.example.projekt.model.User;
import com.example.projekt.service.InformationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/information")
public class InformationController {

    private final InformationService informationService;

    public InformationController(InformationService informationService) {
        this.informationService = informationService;
    }

    @GetMapping
    @PreAuthorize("hasRole('FULL')")
    public String listInformation(Model model,
                                  @AuthenticationPrincipal User user,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String sortDir,
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

        model.addAttribute("information", informationService.getInformationsForUser(user, sortBy, sortDir));
        model.addAttribute("newInformation", new Information());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "information/list";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('FULL')")
    public String createInformation(@RequestParam String title,
                                    @RequestParam String content,
                                    @AuthenticationPrincipal User user) {
        informationService.createInformation(title, content, user);
        return "redirect:/information"; // was /informations
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('FULL')")
    public String editForm(@PathVariable Long id,
                           Model model,
                           @AuthenticationPrincipal User user) {
        model.addAttribute("information", informationService.getInformationById(id, user));
        return "information/edit"; // was informations/edit
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('FULL')")
    public String editInformation(@PathVariable Long id,
                                  @RequestParam String title,
                                  @RequestParam String content,
                                  @AuthenticationPrincipal User user) {
        informationService.updateInformation(id, title, content, user);
        return "redirect:/information"; // was /informations
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
