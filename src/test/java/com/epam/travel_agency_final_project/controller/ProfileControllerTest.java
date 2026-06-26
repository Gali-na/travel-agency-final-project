package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.controller.ProfileController;
import com.epam.travel_agency_final_project.dto.UserProfileDTO;
import com.epam.travel_agency_final_project.exeption.AuthenticationTokenMissingException;
import com.epam.travel_agency_final_project.exeption.JwtAuthenticationException;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.CookieService;
import com.epam.travel_agency_final_project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    private ProfileController controller;

    @Mock
    private UserService userService;
    @Mock
    private JwtProvider jwtTokenProvider;
    @Mock
    private CookieService cookieService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        controller = new ProfileController(userService, jwtTokenProvider, cookieService);
    }
    @Test
    void topUpBalance_InvalidAmount_RedirectsWithError() {
        String view = controller.topUpBalance(request, new BigDecimal("-5.00"));
        assertEquals("redirect:/profile?error=invalid_amount", view);
        verifyNoInteractions(cookieService);
    }

    @Test
    void topUpBalance_MissingToken_ThrowsException() {
        when(cookieService.extractCookie(request, "access_token")).thenReturn(null);
        assertThrows(AuthenticationTokenMissingException.class, () -> {
            controller.topUpBalance(request, new BigDecimal("100.00"));
        });
    }

    @Test
    void topUpBalance_InvalidToken_RedirectsToLogin() throws JwtAuthenticationException {
        String token = "invalid.token";
        when(cookieService.extractCookie(request, "access_token")).thenReturn(token);
        when(jwtTokenProvider.getUserIdFromToken(token)).thenThrow(new JwtAuthenticationException("Error"));

        String view = controller.topUpBalance(request, new BigDecimal("100.00"));

        assertEquals("redirect:/login", view);
    }

    @Test
    void topUpBalance_Success_RedirectsWithSuccess() throws JwtAuthenticationException {
        String token = "valid.token";
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        when(cookieService.extractCookie(request, "access_token")).thenReturn(token);
        when(jwtTokenProvider.getUserIdFromToken(token)).thenReturn(userId);

        String view = controller.topUpBalance(request, amount);

        assertEquals("redirect:/profile?success", view);
        verify(userService).increaseBalance(userId, amount);
    }
    @Test
    void showProfile_WhenAccessTokenIsNull_RedirectsToLogin() {
        when(cookieService.extractCookie(request, "access_token")).thenReturn(null);

        String view = controller.showProfile(request, model, "uk", Locale.UK);

        assertEquals("redirect:/login", view);
    }

    @Test
    void showProfile_WhenTokenIsInvalid_RedirectsToLogin() throws JwtAuthenticationException {
        when(cookieService.extractCookie(request, "access_token")).thenReturn("bad-token");
        when(jwtTokenProvider.getUserIdFromToken("bad-token")).thenThrow(new JwtAuthenticationException("Invalid"));

        String view = controller.showProfile(request, model, "uk", Locale.UK);

        assertEquals("redirect:/login", view);
    }

    @Test
    void showProfile_WhenUserIdIsNull_RedirectsToLogin() throws JwtAuthenticationException {
        when(cookieService.extractCookie(request, "access_token")).thenReturn("token");
        when(jwtTokenProvider.getUserIdFromToken("token")).thenReturn(null);

        String view = controller.showProfile(request, model, "uk", Locale.UK);

        assertEquals("redirect:/login", view);
    }

    @Test
    void showProfile_WhenProfileIsNull_RedirectsToRegister() throws JwtAuthenticationException {
        UUID userId = UUID.randomUUID();
        when(cookieService.extractCookie(request, "access_token")).thenReturn("token");
        when(jwtTokenProvider.getUserIdFromToken("token")).thenReturn(userId);
        when(userService.getProfileData(userId, "uk")).thenReturn(null);

        String view = controller.showProfile(request, model, "uk", Locale.UK);

        assertEquals("redirect:/register", view);
    }

    @Test
    void showProfile_Success_ReturnsProfileView() throws JwtAuthenticationException {
        UUID userId = UUID.randomUUID();
        UserProfileDTO profile = new UserProfileDTO();

        when(cookieService.extractCookie(request, "access_token")).thenReturn("token");
        when(jwtTokenProvider.getUserIdFromToken("token")).thenReturn(userId);
        when(userService.getProfileData(userId, "uk")).thenReturn(profile);

        String view = controller.showProfile(request, model, "uk", Locale.UK);

        assertEquals("profile", view);
        verify(model).addAttribute("user", profile);
    }
}