package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.model.Cart;
import com.epam.travel_agency_final_project.service.TourService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final TourService tourService;
    @GetMapping("/cart")
    public String showCart(HttpServletRequest request,
                           Model model,
                           @RequestParam(value = "lang", defaultValue = "uk") String lang) {

        String rawCartJson = "";
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("cart".equals(c.getName())) {
                    try {
                        // НАДВАЖЛИВО: Декодуємо саме через URLDecoder з UTF_8!
                        rawCartJson = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        System.err.println("Помилка декодування куки кошика: " + e.getMessage());
                    }
                    break;
                }
            }
        }

        // Лог для розробника (подивись у консоль IDE при переході на /cart)
        System.out.println("=== Рядок з куки після декодування: " + rawCartJson);

        // 1. Парсимо кошик з кук
        Cart cart = Cart.fromJson(rawCartJson);

        // Отримуємо всі ID турів
        Set<UUID> tourIds = cart.getItems().keySet();

        // Лог для перевірки: чи розпарсилися ID?
        System.out.println("=== Знайдено ID турів у кошику: " + tourIds);

        // 2. Якщо кошик не порожній, йдемо в базу
        List<TourFullDTO> cartTours = new ArrayList<>();
        if (!tourIds.isEmpty()) {
            cartTours = tourService.getToursForCart(tourIds, lang);

            // 3. Додаємо кількість з мапи в DTO
            for (TourFullDTO tour : cartTours) {
                Integer quantity = cart.getItems().get(tour.getId());
                tour.setQuantity(quantity != null ? quantity : 1);
            }
        }
       ;

        // 4. Передаємо дані у модель
        model.addAttribute("cartTours", cartTours);
        model.addAttribute("currentLang", lang);

        return "cart";
    }
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("tourId") UUID tourId,
                                 @RequestParam(value = "lang", defaultValue = "uk") String lang,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        // 1. Отримуємо поточну куку "cart"
        String rawCartJson = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("cart".equals(c.getName())) {
                    rawCartJson = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                    break;
                }
            }
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
        return "redirect:/cart?lang=" + lang;
    }
}