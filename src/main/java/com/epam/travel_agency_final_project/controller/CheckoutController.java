package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.TourService;
import com.epam.travel_agency_final_project.service.UserService;
import com.epam.travel_agency_final_project.service.CookieService;
import com.epam.travel_agency_final_project.service.UserAuthenticationService;
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
    private final CookieService cookieService;
    private final JwtProvider jwtTokenProvider;
    private final UserService userService;
    private final TourService tourService;
    private final UserAuthenticationService userAuthenticationService;
    @PostMapping("/checkout")
    public String processCheckout(@RequestParam("tourId") UUID tourId,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  Locale locale) {
        UserSecurityDTO userSecurityDTO=  userAuthenticationService.getAuthenticatedUser(request);
        if (userSecurityDTO.isLocked()) {
            return "redirect:/blocked";
        }
        TourFullDTO tourDTO = tourService.findById(tourId, locale.getLanguage());
        if (tourService.checkPaymentAvailability(userSecurityDTO,tourDTO) < 0) {
           return "redirect:/checkoutInfo";
         }
        userService.finalizePurchase(userSecurityDTO, tourDTO);
        cookieService.updateCartCookieAfterPurchase(tourId,request, response);
        return "redirect:/checkout-success";
    }
}
