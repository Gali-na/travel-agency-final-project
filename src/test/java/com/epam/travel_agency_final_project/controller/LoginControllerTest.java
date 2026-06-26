package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.controller.LoginController;
import com.epam.travel_agency_final_project.dto.LoginDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    private LoginController controller;

    @Mock private UserService userService;
    @Mock private MessageSource messageSource;
    @Mock private JwtProvider jwtProvider;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private CookieService cookieService;
    @Mock private UserAuthenticationService userAuthenticationService;
    @Mock private BindingResult bindingResult;
    @Mock private Model model;
    @Mock private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        controller = new LoginController(userService, messageSource, jwtProvider,
                refreshTokenService, cookieService, userAuthenticationService);
    }

    @Test
    void showLoginPage_ReturnsLoginView() {
        String view = controller.showLoginPage(model);
        assertEquals("login", view);
        verify(model).addAttribute(eq("loginDto"), any(LoginDTO.class));
    }
    @Test
    void loginUser_HasErrors_ReturnsLoginView() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = controller.loginUser(new LoginDTO(), bindingResult, model, response, Locale.UK);
        assertEquals("login", view);
    }

    @Test
    void loginUser_InvalidAuth_ReturnsLoginViewWithError() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("wrong");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.authenticate(dto.getEmail(), dto.getPassword())).thenReturn(false);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Invalid");
        String view = controller.loginUser(dto, bindingResult, model, response, Locale.UK);
        assertEquals("login", view);
        verify(model).addAttribute("error", "Invalid");
    }
    @Test
    void loginUser_UserLocked_RedirectsToBlocked() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("locked@test.com");
        dto.setPassword("Valid123!");
        UserSecurityDTO user = new UserSecurityDTO();
        user.setLocked(true);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.authenticate(dto.getEmail(), dto.getPassword())).thenReturn(true);
        when(userService.findByEmail(dto.getEmail())).thenReturn(user);
        String view = controller.loginUser(dto, bindingResult, model, response, Locale.UK);
        assertEquals("redirect:/blocked", view);
    }
    @Test
    void loginUser_Success_RedirectsToHome() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("ok@test.com");
        dto.setPassword("Valid123!");
        UserSecurityDTO user = new UserSecurityDTO();
        user.setLocked(false);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.authenticate(dto.getEmail(), dto.getPassword())).thenReturn(true);
        when(userService.findByEmail(dto.getEmail())).thenReturn(user);
        when(jwtProvider.generateAccessToken(user)).thenReturn("mocked-jwt-token");
        String view = controller.loginUser(dto, bindingResult, model, response, Locale.UK);
        assertEquals("redirect:/", view);
        verify(refreshTokenService).createRefreshToken(eq(user), anyString());
        verify(cookieService).updateAuthCookies(eq(response), eq("mocked-jwt-token"));
    }
}