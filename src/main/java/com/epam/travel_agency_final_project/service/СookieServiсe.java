package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.exeption.TourNotFoundException;
import com.epam.travel_agency_final_project.model.Cart;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

@Service
public class СookieServiсe {
  public String extractCookieJWT(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public boolean updateCartCookieAfterPurchase( UUID tourId,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
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

        Cart cart = Cart.fromJson(rawCartJson);
        cart.removeTour(tourId);
        String updatedCartJson = URLEncoder.encode(cart.toJson(), StandardCharsets.UTF_8);
        Cookie cartCookie = new Cookie("cart", updatedCartJson);
        cartCookie.setMaxAge(30 * 60);
        cartCookie.setPath("/");
        response.addCookie(cartCookie);
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

