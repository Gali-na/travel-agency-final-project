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

import com.epam.travel_agency_final_project.controller.RegistrationController;
import com.epam.travel_agency_final_project.dto.UserRegistrationDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {
    @Mock
    private Model model;
    @InjectMocks
    private RegistrationController registrationController;
    @Mock
    private UserService userService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private UserAuthenticationService userAuthenticationService;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private HttpServletResponse response;
    @Test
    void showRegistrationForm_ShouldAddUserDtoToModelAndReturnRegisterView() {
        String view = registrationController.showRegistrationForm(model);
        assertEquals("register", view);
        verify(model).addAttribute(eq("userDto"), any(UserRegistrationDTO.class));
    }
    @Test
    void registerUser_ValidationException_ReturnsRegister() throws ValidationException {
        UserRegistrationDTO dto = mock(UserRegistrationDTO.class);
        doThrow(new ValidationException("error.invalid")).when(dto).validate();
        when(messageSource.getMessage("error.invalid", null, Locale.UK)).thenReturn("Invalid data");
        String view = registrationController.registerUser(dto, bindingResult, response, Locale.UK);
        assertEquals("register", view);
        verify(bindingResult).reject(null, "Invalid data");
    }
    @Test
    void registerUser_EmailExists_ReturnsRegister() {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setPassword("Abc12345");
        dto.setEmail("test@mail");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        UserSecurityDTO existingUser = new UserSecurityDTO();
        when(userService.findByEmail("test@mail")).thenReturn(existingUser);
        String view = registrationController.registerUser(dto, bindingResult, response, Locale.UK);
        assertEquals("register", view);
        verify(bindingResult).rejectValue("email", "error.user.exists", "User with this email address already exists");
    }
    @Test
    void registerUser_Success_RedirectsToHome() {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail("test@mail");
        dto.setPassword("Abc12345");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        UUID newUserId = UUID.randomUUID();
        when(userService.findByEmail(dto.getEmail())).thenReturn(null);
        when(userService.registerNewUser(dto)).thenReturn(newUserId);
        String view = registrationController.registerUser(dto, bindingResult, response, Locale.UK);
        assertEquals("redirect:/", view);
        verify(userService).registerNewUser(dto);
        verify(userAuthenticationService).registerAndAuthenticate(newUserId, response);
        verifyNoInteractions(bindingResult);
    }
}