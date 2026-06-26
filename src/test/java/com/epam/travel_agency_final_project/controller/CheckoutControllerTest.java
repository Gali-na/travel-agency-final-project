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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    private CheckoutController controller;

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

    @BeforeEach
    void setUp() {

        controller = new CheckoutController(cookieService, null, userService, tourService, userAuthenticationService);
    }

    @Test
    void processCheckout_LockedUser_RedirectsToBlocked() {
        UserSecurityDTO user = new UserSecurityDTO();
        user.setLocked(true);
        when(userAuthenticationService.getAuthenticatedUser(request)).thenReturn(user);

        String view = controller.processCheckout(UUID.randomUUID(), request, response, Locale.UK);

        assertEquals("redirect:/blocked", view);
    }

    @Test
    void processCheckout_InsufficientFunds_RedirectsToInfo() {
        UserSecurityDTO user = new UserSecurityDTO();
        user.setLocked(false);
        user.setBalance(BigDecimal.TEN);
        TourFullDTO tour = new TourFullDTO();
        tour.setPrice(new BigDecimal("100.00"));

        when(userAuthenticationService.getAuthenticatedUser(request)).thenReturn(user);
        when(tourService.findById(any(), anyString())).thenReturn(tour);

        String view = controller.processCheckout(UUID.randomUUID(), request, response, Locale.UK);

        assertEquals("redirect:/checkoutInfo", view);
        verify(userService, never()).finalizePurchase(any(), any());
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

        String view = controller.processCheckout(tourId, request, response, Locale.UK);

        assertEquals("redirect:/checkout-success", view);
        verify(userService).finalizePurchase(user, tour);
        verify(cookieService).updateCartCookieAfterPurchase(tourId, request, response);
    }
}