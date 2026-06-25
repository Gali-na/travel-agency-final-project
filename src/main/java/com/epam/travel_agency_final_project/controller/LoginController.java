package com.epam.travel_agency_final_project.controller;


import com.epam.travel_agency_final_project.dto.LoginDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.CookieService;
import com.epam.travel_agency_final_project.service.RefreshTokenService;
import com.epam.travel_agency_final_project.service.UserAuthenticationService;
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
    private final CookieService cookieService;
    private final UserAuthenticationService userAuthenticationService;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginDto", new LoginDTO());
        return "login";
    }
    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("loginDto") LoginDTO loginDto,
                            BindingResult bindingResult,
                            Model model,
                            HttpServletResponse response,
                            Locale locale) {

        if (bindingResult.hasErrors()) {
            return "login";
        }
        boolean isAuthenticated = userService.authenticate(loginDto.getEmail(), loginDto.getPassword());
        if (!isAuthenticated) {
            String errorMessage = messageSource.getMessage("error.login.invalid", null, locale);
            model.addAttribute("error", errorMessage);
            return "login";
        }
        UserSecurityDTO userSecurityDTO = userService.findByEmail(loginDto.getEmail());
        if (userSecurityDTO.isLocked()) {
            return "redirect:/blocked";
        }
        String accessToken = jwtProvider.generateAccessToken(userSecurityDTO);
        String refreshToken = UUID.randomUUID().toString();
        refreshTokenService.createRefreshToken(userSecurityDTO, refreshToken);
        cookieService.updateAuthCookies(response, accessToken, refreshToken);

        return "redirect:/";
    }
}
