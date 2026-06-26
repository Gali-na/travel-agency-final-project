package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.controller.HomeController;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    private HomeController controller;

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private CookieService cookieService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        controller = new HomeController(jwtProvider, cookieService);
    }

    @Test
    void index_NoToken_SetsFlagsToFalse() {
        when(cookieService.extractCookie(request, "access_token")).thenReturn(null);

        String viewName = controller.index(request, model);

        assertEquals("index", viewName);
        verify(model).addAttribute("isAdmin", false);
        verify(model).addAttribute("isManager", false);
    }

    @Test
    void index_AdminToken_SetsFlagsToTrue() {
        String token = "admin.token";
        when(cookieService.extractCookie(request, "access_token")).thenReturn(token);
        when(jwtProvider.validateAccessToken(token)).thenReturn(true);
        when(jwtProvider.getRolesFromToken(token)).thenReturn(List.of("ROLE_ADMIN"));

        controller.index(request, model);

        verify(model).addAttribute("isAdmin", true);
        verify(model).addAttribute("isManager", true);
    }

    @Test
    void index_UserToken_SetsFlagsToFalse() {
        String token = "user.token";
        when(cookieService.extractCookie(request, "access_token")).thenReturn(token);
        when(jwtProvider.validateAccessToken(token)).thenReturn(true);
        when(jwtProvider.getRolesFromToken(token)).thenReturn(List.of("ROLE_USER"));

        controller.index(request, model);

        verify(model).addAttribute("isAdmin", false);
        verify(model).addAttribute("isManager", false);
    }
}
