package com.epam.travel_agency_final_project.exeption;

import com.epam.travel_agency_final_project.dto.UserRegistrationDTO;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserAlreadyExists(UserAlreadyExistsException ex, Model model, Locale locale) {
        String errorMessage = messageSource.getMessage(ex.getMessage(), null, locale);
        model.addAttribute("error", errorMessage);
        model.addAttribute("userDto", new UserRegistrationDTO());
        return "register";
    }

    @ExceptionHandler(AuthenticationTokenMissingException.class)
    public String handleAuthException(AuthenticationTokenMissingException ex) {
        return "redirect:/login?error=session_expired";
    }
    @ExceptionHandler(CityNotFoundException.class)
    public String handleAuthException(CityNotFoundException ex) {
        return "redirect:/login?error=session_expired";
    }
    @ExceptionHandler(TourNotFoundException.class)
    public String handleTourNotFound(TourNotFoundException ex) {
        return "redirect:/tour-not-found";
    }
    @ExceptionHandler(EmailNotFoundException.class)
    public String handleEmailNotFound() {
        return "admin/invalid-email";
    }
}
