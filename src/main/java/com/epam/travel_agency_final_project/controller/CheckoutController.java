package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.exeption.AuthenticationTokenMissingException;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.TourServiceImpl;
import com.epam.travel_agency_final_project.service.UserService;
import com.epam.travel_agency_final_project.service.СookieServiсe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class CheckoutController {

    // дістаємо з кук аксес токен
    //якщо він відсутній кидаємо  AuthenticationTokenMissingException

    // з токену дістаємо Id клієнта
    //  йдемо в БД ы перевыряэмо чи cyє клієнт з  Id клієнта
    //якщо не існує викидаємоо виключення AuthenticationTokenMissingException
    // якщо існеє перевіряємо чи не заблокований користувач, якщо заблокований -редірект на сторінку /blocked

    // дістаємо Userbalance клієнта

    // дістаємо тур за tourId
    // дістаємо price з туру

    // перевіряємо чи Userbalance>=tour Price

    //якщо Userbalance меншнн -редірект на сторінку /checkoutInfo з надписом, недостатньо коштів

    //СТВОРЮЄМО ОПЛАТУ
    //Userbalance- tour Price перезбергамо в базі баланс юзера
    // робимо запис в таблицю

        /*
CREATE TABLE user_tours (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    user_id UUID NOT NULL,
    tour_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (tour_id) REFERENCES tours(id)
);
*/
    // йдемо в куки перевіряємо дістаєсо куку з надписом "cart"
    //парсимо його і перезаписуємо куку за мінусом туру

    private final СookieServiсe cookieServiсe;
    private final JwtProvider jwtTokenProvider;

    private final UserService userService;
    private final TourServiceImpl tourServiceImpl;


    @PostMapping("/checkout")
    public String processCheckout(@RequestParam("tourId") UUID tourId,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  Locale locale) {

        // 1. Отримання токена
        String accessToken = cookieServiсe.extractCookieJWT(request, "access_token");
        if (accessToken == null) {
            throw new AuthenticationTokenMissingException("Access token is missing");
        }


        // 2. Отримання ID та користувача
        UUID userId = jwtTokenProvider.getUserIdFromToken(accessToken);

         UserSecurityDTO userSecurityDTO = userService.findById(userId);
        if (userSecurityDTO == null) {
            throw new AuthenticationTokenMissingException("Access token is missing");
        }

        // .orElseThrow(() -> new AuthenticationTokenMissingException("User not found"));



        // 3. Перевірка блокування
        if (userSecurityDTO.isLocked()) {
            return "redirect:/blocked";
        }

        // 4. Отримання туру та ціни
      TourFullDTO tourDTO = tourServiceImpl.findById(tourId, locale.getLanguage());
//                .orElseThrow(() -> new IllegalArgumentException("Tour not found"));
//        BigDecimal tourPrice = tour.getPrice();

        // 5. Перевірка балансу

        BigDecimal balance = (userSecurityDTO.getBalance() != null) ? userSecurityDTO.getBalance() : BigDecimal.ZERO;
        BigDecimal price = (tourDTO.getPrice() != null) ? tourDTO.getPrice() : BigDecimal.ZERO;

        System.out.println("**************************************");
        System.out.println(" balance      "+balance);
        System.out.println(" price      "+price);

        System.out.println("**************************************");

        if (balance.compareTo(price) < 0) {
            return "redirect:/checkoutInfo";
        }

//        if (userSecurityDTO.getBalance().compareTo(tourDTO.getPrice()) < 0) {
//            return "redirect:/checkoutInfo";
//        }

        // 6. Оплата: оновлення балансу та запис у user_tours
        // Використовуємо транзакцію для безпеки
        userService.finalizePurchase(userSecurityDTO, tourDTO);

        // 7. Видалення туру з кошика в куках
        cookieServiсe.updateCartCookieAfterPurchase(tourId,request, response);

        return "redirect:/checkout-success";
    }
}
