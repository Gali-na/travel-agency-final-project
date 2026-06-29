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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;
@Slf4j
@Controller
@RequiredArgsConstructor
public class CheckoutController {
    private final CookieService cookieService;
    private final UserService userService;
    private final TourService tourService;
    private final UserAuthenticationService userAuthenticationService;
    @PostMapping("/checkout")
    public String processCheckout(@RequestParam("tourId") UUID tourId,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  Locale locale) {
        log.info("Processing checkout request for tourId: {} by user", tourId);
        UserSecurityDTO userSecurityDTO=  userAuthenticationService.getAuthenticatedUser(request);
        if (userSecurityDTO.isLocked()) {
            log.warn("Blocked user attempt to checkout: {}", userSecurityDTO.getLogin());
            return "redirect:/blocked";
        }
        log.info("Finalizing purchase for user: {} and tour: {}", userSecurityDTO.getLogin(), tourId);
        TourFullDTO tourDTO = tourService.findById(tourId, locale.getLanguage());
        if (tourService.checkPaymentAvailability(userSecurityDTO,tourDTO) < 0) {
            log.info("Payment validation failed for user: {}. Insufficient funds or tour unavailable.", userSecurityDTO.getLogin());
            return "redirect:/checkoutInfo";
         }
        log.info("Finalizing purchase for user: {} and tour: {}", userSecurityDTO.getLogin(), tourId);
        userService.finalizePurchase(userSecurityDTO, tourDTO);
        cookieService.updateCartCookieAfterPurchase(tourId,request, response);
        return "redirect:/checkout-success";
    }
}
