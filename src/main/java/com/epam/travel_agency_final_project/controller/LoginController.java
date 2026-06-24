package com.epam.travel_agency_final_project.controller;


import com.epam.travel_agency_final_project.dto.LoginDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.RefreshTokenService;
import com.epam.travel_agency_final_project.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import java.util.Locale;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
public class LoginController {
    private final UserService userService;
    private final MessageSource messageSource;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        // Цей рядок є ОБОВ'ЯЗКОВИМ!
        // Без нього ви отримуєте помилку, бо thymeleaf не знає, що таке "loginDto"
        model.addAttribute("loginDto", new LoginDTO());

        return "login"; // назва файлу login.html
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("loginDto") LoginDTO loginDto,
                            BindingResult bindingResult,
                            Model model,
                            HttpServletResponse response, // Потрібно для встановлення кук
                            Locale locale) {



        if (bindingResult.hasErrors()) {

            return "login";
        }

        // 1. Перевірка пароля та пошти
        boolean isAuthenticated = userService.authenticate(loginDto.getEmail(), loginDto.getPassword());



        if (!isAuthenticated) {
            String errorMessage = messageSource.getMessage("error.login.invalid", null, locale);
            model.addAttribute("error", errorMessage);
            return "login";

        }

        // 2. Отримуємо користувача, щоб перевірити статус
        UserSecurityDTO userSecurityDTO = userService.findByEmail(loginDto.getEmail());

        // 3. ПЕРЕВІРКА НА БЛОКУВАННЯ
        if (userSecurityDTO.isLocked()) {
            return "redirect:/blocked"; // Перенаправляємо на сторінку блокування
        }

        String accessToken = jwtProvider.generateAccessToken(userSecurityDTO);
        String refreshToken = UUID.randomUUID().toString();
        refreshTokenService.createRefreshToken(userSecurityDTO, refreshToken);
        updateAuthCookies(response, accessToken, refreshToken);
        return "redirect:/";
    }

    private void updateAuthCookies(HttpServletResponse response, String accessToken, String refreshUUID) {

        Cookie refreshCookie = new Cookie("refresh_token", refreshUUID);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(30 * 24 * 60 * 60);
        response.addCookie(refreshCookie);

        Cookie accessCookie = new Cookie("access_token",accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(30 * 24 * 60 * 60);
        response.addCookie(accessCookie);

    }


}

