package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.controller.CheckoutController;
import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {
    @Mock
    private CookieService cookieService;
    @Mock
    private UserService userService;
    @Mock
    private TourService tourService;
    @Mock
    private UserAuthenticationService userAuthenticationService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @InjectMocks
    private CheckoutController checkoutController;
    @Test
    void processCheckout_LockedUser_RedirectsToBlocked() {
        UserSecurityDTO user = new UserSecurityDTO();
        user.setLocked(true);
        when(userAuthenticationService.getAuthenticatedUser(request)).thenReturn(user);

        String view =  checkoutController.processCheckout(UUID.randomUUID(), request, response, Locale.UK);

        assertEquals("redirect:/blocked", view);
    }
    @Test
    void processCheckout_Success_RedirectsToSuccess() {
        UUID tourId = UUID.randomUUID();
        UserSecurityDTO user = new UserSecurityDTO();
        user.setLocked(false);
        user.setBalance(new BigDecimal("200.00"));
        TourFullDTO tour = new TourFullDTO();
        tour.setPrice(new BigDecimal("100.00"));
        when(userAuthenticationService.getAuthenticatedUser(request)).thenReturn(user);
        when(tourService.findById(eq(tourId), anyString())).thenReturn(tour);
        String view =  checkoutController.processCheckout(tourId, request, response, Locale.UK);
        assertEquals("redirect:/checkout-success", view);
        verify(userService).finalizePurchase(user, tour);
        verify(cookieService).updateCartCookieAfterPurchase(tourId, request, response);
    }
    @Test
    void processCheckout_WhenUserIsLocked_ShouldRedirectToBlocked() {
        UUID tourId = UUID.randomUUID();
        Locale locale = Locale.UK;
        UserSecurityDTO lockedUser = new UserSecurityDTO();
        lockedUser.setLocked(true);
        when(userAuthenticationService.getAuthenticatedUser(request)).thenReturn(lockedUser);
        String result = checkoutController.processCheckout(tourId, request, response, locale);
        assertEquals("redirect:/blocked", result);
        verifyNoInteractions(tourService);
        verifyNoInteractions(userService);
    }
    @Test
    void processCheckout_InsufficientFunds_RedirectsToCheckoutInfo() {
        UUID tourId = UUID.randomUUID();
        UserSecurityDTO user = new UserSecurityDTO();
        user.setLocked(false);
        TourFullDTO tour = new TourFullDTO();
        when(userAuthenticationService.getAuthenticatedUser(request)).thenReturn(user);
        when(tourService.findById(eq(tourId), anyString())).thenReturn(tour);
        when(tourService.checkPaymentAvailability(user, tour)).thenReturn(-1);
        String view = checkoutController.processCheckout(tourId, request, response, Locale.UK);
        assertEquals("redirect:/checkoutInfo", view);
        verify(userService, never()).finalizePurchase(any(), any());
        verify(cookieService, never()).updateCartCookieAfterPurchase(any(), any(), any());
    }
}