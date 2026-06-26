package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.controller.RegistrationController;
import com.epam.travel_agency_final_project.dto.UserRegistrationDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.mapper.UserSecurityMapper;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    private RegistrationController controller;

    @Mock
    private UserService userService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private UserSecurityMapper userSecurityMapper;
    @Mock
    private CookieService cookieService;
    @Mock
    private UserAuthenticationService userAuthenticationService;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private Model model;
    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        controller = new RegistrationController(userService, messageSource, jwtProvider,
                refreshTokenService, userSecurityMapper, cookieService, userAuthenticationService);
    }

    @Test
    void showRegistrationForm_ReturnsRegisterView() {
        String view = controller.showRegistrationForm(model);
        assertEquals("register", view);
        verify(model).addAttribute(eq("userDto"), any(UserRegistrationDTO.class));
    }

    @Test
    void registerUser_InvalidInput_ReturnsRegisterView() {
        UserRegistrationDTO dto = mock(UserRegistrationDTO.class);
        doThrow(new ValidationException("error.invalid")).when(dto).validate();
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Invalid data");

        String view = controller.registerUser(dto, bindingResult, response, Locale.UK);

        assertEquals("register", view);
        verify(bindingResult).reject(isNull(), eq("Invalid data"));
    }
    @Test
    void registerUser_LockedUser_RedirectsToLogin() {
        UserRegistrationDTO dto = spy(new UserRegistrationDTO());
        dto.setEmail("locked@test.com");
        dto.setPassword("Valid123!");
        doNothing().when(dto).validate();
        UUID userId = UUID.randomUUID();
        UserSecurityDTO user = new UserSecurityDTO();
        user.setLocked(true);

        when(userService.findByEmail(anyString())).thenReturn(null);
        when(userService.registerNewUser(dto)).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(user);
        String view = controller.registerUser(dto, bindingResult, response, Locale.UK);
        assertEquals("redirect:/login?blocked", view);
    }
    @Test
    void registerUser_Success_RedirectsToHome() {
        UserRegistrationDTO dto = spy(new UserRegistrationDTO());
        dto.setEmail("new@test.com");
        dto.setPassword("Valid123!");
        doNothing().when(dto).validate();

        UUID userId = UUID.randomUUID();
        UserSecurityDTO user = new UserSecurityDTO();
        user.setLocked(false);

        when(userService.findByEmail(anyString())).thenReturn(null);
        when(userService.registerNewUser(any())).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(user);
        String view = controller.registerUser(dto, bindingResult, response, Locale.UK);
        assertEquals("redirect:/", view);
        verify(userAuthenticationService).registerAndAuthenticate(eq(userId), eq(response));
    }

    @Test
    void registerUser_UserExists_ReturnsRegisterView() {
        UserRegistrationDTO dto = spy(new UserRegistrationDTO());
        dto.setEmail("exists@test.com");
        dto.setPassword("Valid123!");
        doNothing().when(dto).validate();

        when(userService.findByEmail("exists@test.com")).thenReturn(new UserSecurityDTO());
        String view = controller.registerUser(dto, bindingResult, response, Locale.UK);
        assertEquals("register", view);
        verify(bindingResult).rejectValue(eq("email"), eq("error.user.exists"), anyString());
    }
}