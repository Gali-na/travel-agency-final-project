package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.model.Cart;
import com.epam.travel_agency_final_project.service.CookieService;
import com.epam.travel_agency_final_project.service.TourService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequiredArgsConstructor
public class CartController {
    private final CookieService cookieService;
    private final TourService tourService;
    @GetMapping("/cart")
    public String showCart(HttpServletRequest request,
                           Model model,
                           @RequestParam(value = "lang", defaultValue = "uk") String lang) {
        log.info("Displaying cart for request. Language: {}", lang);
        Cart cart = Cart.fromJson(cookieService.parseCookieDecoder(request.getCookies()));
        Set<UUID> tourIds = cart.getItems().keySet();
        log.debug("Cart retrieved with {} items", tourIds.size());
        List<TourFullDTO> cartTours = new ArrayList<>();
        if (!tourIds.isEmpty()) {
            cartTours = tourService.getToursForCart(tourIds, lang);
            log.debug("Fetched {} tours from database for the cart", cartTours.size());
            for (TourFullDTO tour : cartTours) {
                Integer quantity = cart.getItems().get(tour.getId());
                tour.setQuantity(quantity != null ? quantity : 1);
            }
        } else {
            log.debug("Cart is empty");
        }
        model.addAttribute("cartTours", cartTours);
        model.addAttribute("currentLang", lang);
        return "cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("tourId") UUID tourId,
                                 @RequestParam(value = "lang", defaultValue = "uk") String lang,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        Cart cart = Cart.fromJson(cookieService.parseCookieDecoder(request.getCookies()));
        cart.removeTour(tourId);
        String updatedCartJson = URLEncoder.encode(cart.toJson(), StandardCharsets.UTF_8);
        Cookie cartCookie = new Cookie("cart", updatedCartJson);
        cartCookie.setMaxAge(30 * 60);
        cartCookie.setPath("/");
        response.addCookie(cartCookie);
        return "redirect:/cart?lang=" + lang;
    }
}