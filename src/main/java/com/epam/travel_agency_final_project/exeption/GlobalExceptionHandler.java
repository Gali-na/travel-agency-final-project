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
        // Отримуємо перекладений текст помилки
        String errorMessage = messageSource.getMessage(ex.getMessage(), null, locale);
        model.addAttribute("error", errorMessage);

        // Повертаємо об'єкт для форми, щоб поля не зникли (якщо ви хочете заповнити їх)
        model.addAttribute("userDto", new UserRegistrationDTO());

        return "register";
    }

    @ExceptionHandler(AuthenticationTokenMissingException.class)
    public String handleAuthException(AuthenticationTokenMissingException ex) {
        // Перенаправляємо користувача на логін, якщо немає куки
        return "redirect:/login?error=session_expired";
    }
    @ExceptionHandler(CityNotFoundException.class)
    public String handleAuthException(CityNotFoundException ex) {
        return "redirect:/login?error=session_expired";
    }



    @ExceptionHandler(TourNotFoundException.class)
    public String handleTourNotFound(TourNotFoundException ex) {
        // Перенаправляємо на сторінку, яка повідомляє, що тур відсутній
        return "redirect:/tour-not-found";
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public String handleEmailNotFound() {
        // Thymeleaf автоматично знайде шаблон у src/main/resources/templates/error/invalid-email.html
        return "admin/invalid-email";
    }

//    @ExceptionHandler(ExpiredJwtException.class)
//    public String handleExpiredJwt(ExpiredJwtException ex) {
//        // Користувача перенаправлено на логін через прострочення токена
//        return "redirect:/login?message=session_expired";
//    }

    // Можна також додати обробку загальних помилок безпеки
//    @ExceptionHandler(AuthenticationTokenMissingException.class)
//    public String handleAuthMissing(AuthenticationTokenMissingException ex) {
//        return "redirect:/login";
//    }
}
