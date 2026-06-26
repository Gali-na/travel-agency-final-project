package com.epam.travel_agency_final_project.controller;
import com.epam.travel_agency_final_project.dto.UserProfileDTO;
import com.epam.travel_agency_final_project.exeption.AuthenticationTokenMissingException;
import com.epam.travel_agency_final_project.exeption.JwtAuthenticationException;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.CookieService;
import com.epam.travel_agency_final_project.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
public class ProfileController {
    private final UserService userService;
    private final JwtProvider jwtTokenProvider;
    private final CookieService cookieService;
    @GetMapping("/profile")
    public String showProfile(HttpServletRequest request,
                              Model model,
                              @RequestParam(defaultValue = "uk") String lang, Locale locale) {

        String accessToken = cookieService.extractCookie(request, "access_token");
        if (accessToken == null) {
            return "redirect:/login";
        }
        UUID userId = null;
        try {
            userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        } catch (JwtAuthenticationException e) {
            return "redirect:/login";
        }

        if(userId==null) {
            return "redirect:/login";
        }
        UserProfileDTO profile = userService.getProfileData(userId,"uk");

        if(profile==null){
            return "redirect:/register";
        }

        model.addAttribute("user", profile);
        model.addAttribute("userTours", profile.getUserTours());
        return "profile";
    }

    @PostMapping("/profile/top-up")
    public String topUpBalance(HttpServletRequest request, @RequestParam("amount") BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "redirect:/profile?error=invalid_amount";
        }
        String accessToken = cookieService.extractCookie(request, "access_token");
        if (accessToken == null) {
            throw new AuthenticationTokenMissingException("Access token is missing in cookies");
        }
        UUID userIdFromToken = null;
        try {
            userIdFromToken = jwtTokenProvider.getUserIdFromToken(accessToken);
        } catch (JwtAuthenticationException e) {
            return "redirect:/login";
        }
        userService.increaseBalance(userIdFromToken, amount);

        return "redirect:/profile?success";
    }
}
