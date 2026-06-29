package com.epam.travel_agency_final_project.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.epam.travel_agency_final_project.controller.ProfileController;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.dto.UserProfileDTO;
import com.epam.travel_agency_final_project.service.UserAuthenticationService;
import com.epam.travel_agency_final_project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private UserAuthenticationService userAuthenticationService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Model model;
    @InjectMocks
    private ProfileController profileController;
    @Test
    void showProfile_UserFound_ReturnsProfileView() {
        UUID userId = UUID.randomUUID();
        UserSecurityDTO securityDTO = new UserSecurityDTO();
        securityDTO.setId(userId);
        UserProfileDTO profileDTO = new UserProfileDTO();
        when(userAuthenticationService.getAuthenticatedUser(request)).thenReturn(securityDTO);
        when(userService.getProfileData(userId, "uk")).thenReturn(profileDTO);
        String view = profileController.showProfile(request, model);
        assertEquals("profile", view);
        verify(model).addAttribute("user", profileDTO);
    }

    @Test
    void showProfile_UserNotFound_RedirectsToRegister() {
        UUID userId = UUID.randomUUID();
        UserSecurityDTO securityDTO = new UserSecurityDTO();
        securityDTO.setId(userId);
        when(userAuthenticationService.getAuthenticatedUser(request)).thenReturn(securityDTO);
        when(userService.getProfileData(userId, "uk")).thenReturn(null);
        String view = profileController.showProfile(request, model);
        assertEquals("redirect:/register", view);
    }

    @Test
    void topUpBalance_ValidAmount_RedirectsWithSuccess() {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        UserSecurityDTO securityDTO = new UserSecurityDTO();
        securityDTO.setId(userId);
        when(userAuthenticationService.getAuthenticatedUser(request)).thenReturn(securityDTO);
        String view = profileController.topUpBalance(request, amount);
        assertEquals("redirect:/profile?success", view);
        verify(userService).increaseBalance(userId, amount);
    }

    @Test
    void topUpBalance_InvalidAmount_RedirectsWithError() {
        String view = profileController.topUpBalance(request, new BigDecimal("-10.00"));
        assertEquals("redirect:/profile?error=invalid_amount", view);
        verifyNoInteractions(userService);
    }
}