package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.dto.UserRegistrationDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.entity.User;
import com.epam.travel_agency_final_project.mapper.UserSecurityMapper;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.RefreshTokenService;
import com.epam.travel_agency_final_project.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Locale;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final MessageSource messageSource;
    private final JwtProvider jwtProvider; // Додаємо для створення токенів
    private final RefreshTokenService refreshTokenService; // Для створення рефреш-токена
    private final UserSecurityMapper userSecurityMapper; // Для конвертації юзера в DTO

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Створюємо порожній об'єкт для прив'язки до форми (th:object="${userDto}")
        model.addAttribute("userDto", new UserRegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDto") UserRegistrationDTO userDto,
                               BindingResult bindingResult,
                               HttpServletResponse response, // Потрібно для встановлення кук
                               Locale locale) {

        if (bindingResult.hasErrors()) return "register";

        try {
            userDto.validate();

            if (userService.findByEmail(userDto.getEmail())!=null) {
                bindingResult.rejectValue("email", "error.user.exists", "Користувач з таким email вже існує");
                return "register";
            }
            // 1. Створюємо користувача
            UUID userId = userService.registerNewUser(userDto);

            // 2. Перевіряємо, чи не заблокований (хоча при створенні він має бути активним)
            if (userService.findById(userId).isLocked()) {
                return "redirect:/login?blocked";
            }

            // 3. Генеруємо токени
            UserSecurityDTO userSecurityDTO = userService.findById(userId);
            String accessToken = jwtProvider.generateAccessToken(userSecurityDTO);
            String refreshToken = UUID.randomUUID().toString();

            // 4. Зберігаємо refresh token в БД
            refreshTokenService.createRefreshToken(userSecurityDTO, refreshToken);

            // 5. Встановлюємо куки
            updateAuthCookies(response, accessToken, refreshToken);

            return "redirect:/"; // Успішний вхід

        } catch (ValidationException e) {
            bindingResult.reject(null, messageSource.getMessage(e.getMessage(), null, locale));
            return "register";
        }
    }

    private void updateAuthCookies(HttpServletResponse response, String accessToken, String refreshUUID) {
        Cookie accessCookie = new Cookie("access_token", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(15 * 60);

        Cookie refreshCookie = new Cookie("refresh_token", refreshUUID);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(30 * 24 * 60 * 60);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }
}
