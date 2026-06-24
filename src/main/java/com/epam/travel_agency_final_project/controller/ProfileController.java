package com.epam.travel_agency_final_project.controller;


import com.epam.travel_agency_final_project.dto.UserProfileDTO;
import com.epam.travel_agency_final_project.exeption.AuthenticationTokenMissingException;
import com.epam.travel_agency_final_project.security.JwtProvider;
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
    private final JwtProvider jwtTokenProvider; // Ваш клас для роботи з JWT

    @GetMapping("/profile")
    public String showProfile(HttpServletRequest request,
                              Model model,
                              @RequestParam(defaultValue = "uk") String lang, Locale locale) {



        // 1. Шукаємо куку з ім'ям "access_token"
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            return "redirect:/login";
        }

        // 3. Дістаємо userId з токена (використовуємо ваш існуючий JwtTokenProvider)
        UUID userId = jwtTokenProvider.getUserIdFromToken(token);

        if(userId==null) {
            return "redirect:/login";
        }
        // 4. Отримуємо дані профілю


        UserProfileDTO profile = userService.getProfileData(userId,"uk");




      //  model.addAttribute("user", profile);
        model.addAttribute("user", profile);
        model.addAttribute("userTours", profile.getUserTours());
        return "profile";
    }



    @PostMapping("/profile/top-up")
    public String topUpBalance(HttpServletRequest request,
                               @RequestParam("amount") BigDecimal amount) {

        // 1. Валідація суми
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "redirect:/profile?error=invalid_amount";
        }

        // 2. Отримання куки з перевіркою
        String accessToken = extractCookie(request, "access_token");

        if (accessToken == null) {
            throw new AuthenticationTokenMissingException("Access token is missing in cookies");
        }

        // 3. Логіка поповнення (використовуємо токен, якщо потрібно для безпеки)
          UUID userIdFromToken = jwtTokenProvider.getUserIdFromToken(accessToken);
         userService.increaseBalance(userIdFromToken, amount);

        return "redirect:/profile?success";
    }


    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
