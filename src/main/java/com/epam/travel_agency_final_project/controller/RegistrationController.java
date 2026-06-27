package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.dto.UserRegistrationDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.mapper.UserSecurityMapper;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.CookieService;
import com.epam.travel_agency_final_project.service.RefreshTokenService;
import com.epam.travel_agency_final_project.service.UserAuthenticationService;
import com.epam.travel_agency_final_project.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
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
    private final UserAuthenticationService userAuthenticationService;
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDTO());
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDto") UserRegistrationDTO userDto,
                               BindingResult bindingResult,
                               HttpServletResponse response,
                               Locale locale) {
        try {userDto.validate();}
        catch (ValidationException e) {
            bindingResult.reject(null, messageSource.getMessage(e.getMessage(), null, locale));
            return "register";
        }
        if (userService.findByEmail(userDto.getEmail())!=null) {
               bindingResult.rejectValue("email", "error.user.exists", "User with this email address already exists");
                return "register";
        }
       UUID userId = userService.registerNewUser(userDto);
        userAuthenticationService.registerAndAuthenticate(userId , response);
        return "redirect:/";
    }
}
