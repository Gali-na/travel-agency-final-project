package com.epam.travel_agency_final_project.controller;
import com.epam.travel_agency_final_project.model.Cart;
import com.epam.travel_agency_final_project.model.TourFilter;
import com.epam.travel_agency_final_project.service.CookieService;
import com.epam.travel_agency_final_project.service.TourService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import com.epam.travel_agency_final_project.dto.TourFullDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
@RequiredArgsConstructor
@Controller
public class TourWebController {
    private final TourService tourService;
    private final CookieService cookieService;
    @GetMapping("/tours")
    public String getToursPage(
            @RequestParam(defaultValue = "uk") String lang,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Boolean isHot,
            @RequestParam(required = false) String tourType,
            @RequestParam(required = false) String hotelType,
            Model model) {
        int pageSize = 2;
        TourFilter tourFilter = new TourFilter(isHot, tourType, hotelType);
        Page<TourFullDTO> toursPage = tourService.getTours(lang, tourFilter, page, pageSize);
        model.addAttribute("tours", toursPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", toursPage.getTotalPages());
        model.addAttribute("currentLang", lang);
        model.addAttribute("filter", tourFilter);
        return "tours";
    }
    @PostMapping("/book")
    public String addToCart(@RequestParam("tourId") UUID tourId,
                            @RequestParam(value = "lang", defaultValue = "uk") String lang,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        Cart cart = cookieService.getCartFromCookie(request);
        cart.addTour(tourId);
        String updatedCartJson = URLEncoder.encode(cart.toJson(), StandardCharsets.UTF_8);
        Cookie cartCookie = new Cookie("cart", updatedCartJson);
        cartCookie.setMaxAge(30 * 60);
        cartCookie.setPath("/");
        response.addCookie(cartCookie);
        return "redirect:/tours?lang=" + lang;
    }
}