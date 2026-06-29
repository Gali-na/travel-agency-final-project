package com.epam.travel_agency_final_project.controller;
import com.epam.travel_agency_final_project.dto.UserProfileDTO;
import com.epam.travel_agency_final_project.service.UserAuthenticationService;
import com.epam.travel_agency_final_project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
@Slf4j
@RequiredArgsConstructor
@Controller
public class ProfileController {
    private final UserService userService;
    private final UserAuthenticationService userAuthenticationService;
    @GetMapping("/profile")
    public String showProfile(HttpServletRequest request, Model model) {
        UUID userId = userAuthenticationService.getAuthenticatedUser(request).getId();
        UserProfileDTO profile = userService.getProfileData(userId,"uk");
        log.info("Fetching profile data for user ID: {}", userId);
        if(profile==null){
            log.warn("Profile not found for user ID: {}. Redirecting to register.", userId);
            return "redirect:/register";
        }
        model.addAttribute("user", profile);
        model.addAttribute("userTours", profile.getUserTours());
        return "profile";
    }
    @PostMapping("/profile/top-up")
    public String topUpBalance(HttpServletRequest request, @RequestParam("amount") BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid top-up attempt  Amount: {}", amount);
            return "redirect:/profile?error=invalid_amount";
        }
        UUID userId = userAuthenticationService.getAuthenticatedUser(request).getId();
        userService.increaseBalance(userId, amount);
        log.info("Balance successfully topped up for user ID: {}", userId);
        return "redirect:/profile?success";
    }
}
