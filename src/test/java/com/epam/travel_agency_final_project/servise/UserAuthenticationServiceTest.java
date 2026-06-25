package com.epam.travel_agency_final_project.servise;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.exeption.AuthenticationTokenMissingException;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.CookieService;
import com.epam.travel_agency_final_project.service.UserAuthenticationService;
import com.epam.travel_agency_final_project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationServiceTest {

    @Mock
    private CookieService cookieService;

    @Mock
    private JwtProvider jwtTokenProvider;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UserAuthenticationService authenticationService;

    @Test
    void getAuthenticatedUser_ShouldReturnUser_WhenTokenIsValid() {
        String token = "valid.jwt.token";
        UUID userId = UUID.randomUUID();
        UserSecurityDTO expectedUser = new UserSecurityDTO();

        when(cookieService.extractCookieJWT(request, "access_token")).thenReturn(token);
        when(jwtTokenProvider.getUserIdFromToken(token)).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(expectedUser);

        UserSecurityDTO result = authenticationService.getAuthenticatedUser(request);

        assertNotNull(result);
        assertEquals(expectedUser, result);
    }

    @Test
    void getAuthenticatedUser_ShouldThrowException_WhenTokenIsMissing() {
        when(cookieService.extractCookieJWT(request, "access_token")).thenReturn(null);

        assertThrows(AuthenticationTokenMissingException.class, () ->
                authenticationService.getAuthenticatedUser(request)
        );
    }
    @Test
    void getAuthenticatedUser_ShouldThrowException_WhenUserDoesNotExist() {
        String token = "valid.jwt.token";
        UUID userId = UUID.randomUUID();

        when(cookieService.extractCookieJWT(request, "access_token")).thenReturn(token);
        when(jwtTokenProvider.getUserIdFromToken(token)).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(null);

        assertThrows(AuthenticationTokenMissingException.class, () ->
                authenticationService.getAuthenticatedUser(request)
        );
    }
}