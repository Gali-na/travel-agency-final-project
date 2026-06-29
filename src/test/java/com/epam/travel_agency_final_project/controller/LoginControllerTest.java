package com.epam.travel_agency_final_project.controller;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.epam.travel_agency_final_project.dto.LoginDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.service.UserAuthenticationService;
import com.epam.travel_agency_final_project.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Locale;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private UserAuthenticationService userAuthenticationService;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private Model model;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private LoginController loginController;

    @Test
    void loginUser_BindingErrors_ReturnsLogin() {
        LoginDTO dto = new LoginDTO();
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = loginController.loginUser(dto, bindingResult, model, response, Locale.UK);
        assertEquals("login", view);
    }

    @Test
    void loginUser_InvalidCredentials_ReturnsLoginWithError() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("wrong");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.authenticate(anyString(), anyString())).thenReturn(false);
        when(messageSource.getMessage(eq("error.login.invalid"), any(), any())).thenReturn("Invalid");
        String view = loginController.loginUser(dto, bindingResult, model, response, Locale.UK);
        assertEquals("login", view);
        verify(model).addAttribute(eq("error"), eq("Invalid"));
    }
    @Test
    void loginUser_LockedUser_RedirectsToBlocked() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("password");
        UserSecurityDTO user = new UserSecurityDTO();
        user.setLocked(true);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.authenticate("user@example.com", "password")).thenReturn(true);
        when(userService.findByEmail("user@example.com")).thenReturn(user);
        String view = loginController.loginUser(dto, bindingResult, model, response, Locale.UK);
        assertEquals("redirect:/blocked", view);
    }

    @Test
    void loginUser_Success_RedirectsToHome() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("password");
        UserSecurityDTO user = new UserSecurityDTO();
        user.setLocked(false);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.authenticate("user@example.com", "password")).thenReturn(true);
        when(userService.findByEmail("user@example.com")).thenReturn(user);
        String view = loginController.loginUser(dto, bindingResult, model, response, Locale.UK);
        assertEquals("redirect:/", view);
        verify(userAuthenticationService).updateRefreshAccessToken(response, user);
    }
    @Test
    void showLoginPage_ShouldAddLoginDtoToModelAndReturnLoginView() {
        String view = loginController.showLoginPage(model);
        assertEquals("login", view);
        verify(model).addAttribute(eq("loginDto"), any(LoginDTO.class));
    }
}