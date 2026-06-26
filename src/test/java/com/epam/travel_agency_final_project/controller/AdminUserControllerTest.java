package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.controller.AdminUserController;
import com.epam.travel_agency_final_project.dto.UserProfileDTO;
import com.epam.travel_agency_final_project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {
    private AdminUserController controller;
    @Mock
    private UserService userService;
    @Mock
    private Model model;
    @Mock
    private Pageable pageable;
    @Mock
    private Page<UserProfileDTO> page;

    @BeforeEach
    void setUp() {
        controller = new AdminUserController(userService);
    }

    @Test
    void listUsers_WithEmail_ReturnsUsersPage() {
        String email = "test@example.com";
        when(userService.findByEmailExact(email, pageable)).thenReturn(page);
        when(page.isEmpty()).thenReturn(false);

        String viewName = controller.listUsers(model, pageable, email);

        assertEquals("admin/users", viewName);
        verify(model).addAttribute("users", page);
        verify(model).addAttribute("email", email);
    }

    @Test
    void listUsers_WithInvalidEmail_ReturnsInvalidEmailView() {
        String email = "wrong@example.com";
        when(userService.findByEmailExact(email, pageable)).thenReturn(page);
        when(page.isEmpty()).thenReturn(true);

        String viewName = controller.listUsers(model, pageable, email);

        assertEquals("admin/invalid-email", viewName);
    }

    @Test
    void listUsers_WithoutEmail_ReturnsAllUsers() {
        when(userService.findAll(pageable)).thenReturn(page);

        String viewName = controller.listUsers(model, pageable, null);

        assertEquals("admin/users", viewName);
        verify(model).addAttribute("users", page);
    }

    @Test
    void lockUser_RedirectsToUsersList() {
        UUID userId = UUID.randomUUID();

        String viewName = controller.lockUser(userId);

        assertEquals("redirect:/admin/users", viewName);
        verify(userService, times(1)).lockUser(userId);
    }
}