package com.epam.travel_agency_final_project.api;

import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.model.Cart;
import com.epam.travel_agency_final_project.model.TourFilter;
import com.epam.travel_agency_final_project.service.CookieService;
import com.epam.travel_agency_final_project.service.TourService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TourRestController {
    private final TourService tourService;
    private final CookieService cookieService;
    @GetMapping("/tours")
    public ResponseEntity<Page<TourFullDTO>> getAllTours(
            @RequestHeader(name = "Accept-Language", defaultValue = "uk") String lang,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(required = false) Boolean isHot,
            @RequestParam(required = false) String tourType,
            @RequestParam(required = false) String hotelType) {
        TourFilter tourFilter = new TourFilter(isHot, tourType, hotelType);
        Page<TourFullDTO> toursPage = tourService.getTours(lang, tourFilter, page, size);
        return ResponseEntity.ok(toursPage);
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addToCart(
            @RequestParam UUID tourId,
            HttpServletRequest request,
            HttpServletResponse response) {

        Cart cart = cookieService.getCartFromCookie(request);
        cart.addTour(tourId);
        String updatedCartJson = URLEncoder.encode(cart.toJson(), StandardCharsets.UTF_8);
        Cookie cartCookie = new Cookie("cart", updatedCartJson);
        cartCookie.setMaxAge(30 * 60);
        cartCookie.setPath("/");
        response.addCookie(cartCookie);
        return ResponseEntity.ok(cart);
    }
}