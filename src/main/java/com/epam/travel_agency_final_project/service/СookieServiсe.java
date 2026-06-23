package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.exeption.TourNotFoundException;
import com.epam.travel_agency_final_project.model.Cart;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

@Service
public class СookieServiсe {

    //// ей метод дублюэться в JwtCookieFilter з ыменем extractCookie
  public String extractCookieJWT(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }


    //// ей метод дублюэться в CartController removeFromCart
    public boolean updateCartCookieAfterPurchase( UUID tourId,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        // 1. Отримуємо поточну куку "cart"
        String rawCartJson = "";
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new TourNotFoundException("tour not found");
        }
            for (Cookie c : cookies) {
                if ("cart".equals(c.getName())) {
                    rawCartJson = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                    break;
                }
            }

        if(rawCartJson.isEmpty()){
            throw new TourNotFoundException("tour not found");
        }

        // 2. Парсимо кошик
        Cart cart = Cart.fromJson(rawCartJson);

        // 3. Видаляємо обраний тур
        cart.removeTour(tourId);

        // 4. Серіалізуємо оновлений кошик назад у JSON
        String updatedCartJson = URLEncoder.encode(cart.toJson(), StandardCharsets.UTF_8);

        // 5. Оновлюємо куку у відповіді сервера
        Cookie cartCookie = new Cookie("cart", updatedCartJson);
        cartCookie.setMaxAge(30 * 60); // 30 хвилин
        cartCookie.setPath("/");
        response.addCookie(cartCookie);

        // Повертаємо користувача назад у кошик, щоб він побачив зміни
        return true ;
    }

    public String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

}

