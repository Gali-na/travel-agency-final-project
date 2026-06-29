package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.dto.RefreshTokenDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.exeption.AuthenticationTokenMissingException;
import com.epam.travel_agency_final_project.security.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.relational.core.sql.When.when;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationServiceTest {
    @Mock
    private CookieService cookieService;
    @Mock
    private UserService userService;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private HttpServletRequest request;
    @Mock
   private JwtProvider jwtProvider;

    @InjectMocks
    private UserAuthenticationService userAuthenticationService;
    @Test
    void updateRefreshAccessToken_ShouldGenerateTokensAndSetCookies() {
        UUID userId = UUID.randomUUID();
        UserSecurityDTO userDto = UserSecurityDTO.builder().id(userId).build();
        String expectedAccessToken = "access-token-123";

        when(jwtProvider.generateAccessToken(userDto)).thenReturn(expectedAccessToken);

        userAuthenticationService.updateRefreshAccessToken(response, userDto);

        verify(jwtProvider).generateAccessToken(userDto);
        verify(refreshTokenService).createRefreshToken(eq(userDto), anyString());
        verify(cookieService).updateAuthCookies(eq(response), eq(expectedAccessToken), anyString());
    }

    @Test
    void getAuthenticatedUser_ShouldThrowException_WhenCookieIsMissing() {
        when(cookieService.extractCookie(request, "refresh_token")).thenReturn(null);

        assertThrows(AuthenticationTokenMissingException.class, () ->
                userAuthenticationService.getAuthenticatedUser(request));
    }

    @Test
    void getAuthenticatedUser_ShouldThrowException_WhenTokenNotFoundInDb() {
        String uuid = "test-uuid";
        when(cookieService.extractCookie(request, "refresh_token")).thenReturn(uuid);
        when(refreshTokenService.getRefreshToken(uuid)).thenReturn(null);

        assertThrows(AuthenticationTokenMissingException.class, () ->
                userAuthenticationService.getAuthenticatedUser(request));
    }

    @Test
    void getAuthenticatedUser_ShouldThrowException_WhenUserNotFound() {
        String uuid = "test-uuid";
        UUID userId = UUID.randomUUID();
        RefreshTokenDTO tokenDto = RefreshTokenDTO.builder().userId(userId).build();

        when(cookieService.extractCookie(request, "refresh_token")).thenReturn(uuid);
        when(refreshTokenService.getRefreshToken(uuid)).thenReturn(tokenDto);
        when(userService.findById(userId)).thenReturn(null);

        assertThrows(AuthenticationTokenMissingException.class, () ->
                userAuthenticationService.getAuthenticatedUser(request));
    }

    @Test
    void getAuthenticatedUser_ShouldReturnUser_WhenAllDataIsValid() {
        String uuid = "test-uuid";
        UUID userId = UUID.randomUUID();
        RefreshTokenDTO tokenDto = RefreshTokenDTO.builder().userId(userId).build();
        UserSecurityDTO userDto = UserSecurityDTO.builder().id(userId).build();

        when(cookieService.extractCookie(request, "refresh_token")).thenReturn(uuid);
        when(refreshTokenService.getRefreshToken(uuid)).thenReturn(tokenDto);
        when(userService.findById(userId)).thenReturn(userDto);

        UserSecurityDTO result = userAuthenticationService.getAuthenticatedUser(request);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void registerAndAuthenticate_ShouldFindUserAndTriggerTokenUpdate() {
        UUID userId = UUID.randomUUID();
        UserSecurityDTO userDto = UserSecurityDTO.builder().id(userId).build();
        String accessToken = "access-token-456";

        when(userService.findById(userId)).thenReturn(userDto);
        when(jwtProvider.generateAccessToken(userDto)).thenReturn(accessToken);

        userAuthenticationService.registerAndAuthenticate(userId, response);

        verify(userService).findById(userId);
        verify(jwtProvider).generateAccessToken(userDto);
        verify(refreshTokenService).createRefreshToken(eq(userDto), anyString());
        verify(cookieService).updateAuthCookies(eq(response), eq(accessToken), anyString());
    }
    @Test
    void registerAndAuthenticate_ShouldThrowException_WhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userService.findById(userId)).thenReturn(null);

        assertThrows(NullPointerException.class, () ->
                userAuthenticationService.registerAndAuthenticate(userId, response));
    }
}