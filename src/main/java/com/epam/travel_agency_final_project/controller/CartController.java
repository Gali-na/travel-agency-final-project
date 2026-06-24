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

        Cart cart = Cart.fromJson(parseCookie(request.getCookies()));
        Set<UUID> tourIds = cart.getItems().keySet();
        List<TourFullDTO> cartTours = new ArrayList<>();
        if (!tourIds.isEmpty()) {
            cartTours = tourService.getToursForCart(tourIds, lang);
            for (TourFullDTO tour : cartTours) {
                Integer quantity = cart.getItems().get(tour.getId());
                tour.setQuantity(quantity != null ? quantity : 1);
            }
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

        Cart cart = Cart.fromJson(parseCookie(request.getCookies()));
        cart.removeTour(tourId);
        String updatedCartJson = URLEncoder.encode(cart.toJson(), StandardCharsets.UTF_8);
        Cookie cartCookie = new Cookie("cart", updatedCartJson);
        cartCookie.setMaxAge(30 * 60);
        cartCookie.setPath("/");
        response.addCookie(cartCookie);
        return "redirect:/cart?lang=" + lang;
    }
    private String parseCookie ( Cookie[] cookies){
        String rawCartJson="";
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("cart".equals(c.getName())) {
                    rawCartJson = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                    break;
                }
            }
        }
        return rawCartJson;
    }
}