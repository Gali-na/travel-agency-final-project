package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.exeption.AuthenticationTokenMissingException;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.TourService;
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
    private final СookieServiсe cookieServiсe;
    private final JwtProvider jwtTokenProvider;
    private final UserService userService;
    private final TourService tourServiceImpl;
    @PostMapping("/checkout")
    public String processCheckout(@RequestParam("tourId") UUID tourId,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  Locale locale) {

        String accessToken = cookieServiсe.extractCookieJWT(request, "access_token");
        if (accessToken == null) {
            throw new AuthenticationTokenMissingException("Access token is missing");
        }
        UUID userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        UserSecurityDTO userSecurityDTO = userService.findById(userId);
        if (userSecurityDTO == null) {
            throw new AuthenticationTokenMissingException("Access token is missing");
        }

        if (userSecurityDTO.isLocked()) {
            return "redirect:/blocked";
        }
        TourFullDTO tourDTO = tourServiceImpl.findById(tourId, locale.getLanguage());
        BigDecimal balance = (userSecurityDTO.getBalance() != null) ? userSecurityDTO.getBalance() : BigDecimal.ZERO;
        BigDecimal price = (tourDTO.getPrice() != null) ? tourDTO.getPrice() : BigDecimal.ZERO;
        if (balance.compareTo(price) < 0) {
            return "redirect:/checkoutInfo";
        }
        userService.finalizePurchase(userSecurityDTO, tourDTO);
        cookieServiсe.updateCartCookieAfterPurchase(tourId,request, response);
        return "redirect:/checkout-success";
    }
}
