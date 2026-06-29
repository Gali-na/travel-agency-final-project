package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.dto.UserProfileDTO;
import com.epam.travel_agency_final_project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.UUID;
@Slf4j
@RequiredArgsConstructor
@Controller
public class AdminUserController {
    private final UserService userService;
    @GetMapping("/admin/users")
    public String listUsers(Model model,
                            @PageableDefault(size = 5) Pageable pageable,
                            @RequestParam(required = false) String email) {
        log.info("Fetching user list. Page: {}, Size: {}, Email filter: {}",
                pageable.getPageNumber(), pageable.getPageSize(), email);
        if (email != null && !email.trim().isEmpty()) {
            log.debug("Searching for user with email: {}", email);
            Page<UserProfileDTO> result = userService.findByEmailExact(email, pageable);
            if (result.isEmpty()) {
                log.warn("No user found with email: {}", email);
                return "admin/invalid-email";
            }
            model.addAttribute("users", result);
            model.addAttribute("email", email);
        } else {
            log.debug("Retrieving all users (no email filter applied)");
            model.addAttribute("users", userService.findAll(pageable));
        }
        return "admin/users";
    }
    @PostMapping("/admin/users/lock/{id}")
    public String lockUser(@PathVariable("id") UUID id) {
        userService.lockUser(id);
        return "redirect:/admin/users";
    }
}