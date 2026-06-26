package com.epam.travel_agency_final_project.servise;

import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.exeption.AuthenticationTokenMissingException;
import com.epam.travel_agency_final_project.exeption.JwtAuthenticationException;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.CookieService;
import com.epam.travel_agency_final_project.service.RefreshTokenService;
import com.epam.travel_agency_final_project.service.UserAuthenticationService;
import com.epam.travel_agency_final_project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    @Mock
    RefreshTokenService refreshTokenService;

    @InjectMocks
    private UserAuthenticationService userAuthenticationService;

    private final String VALID_TOKEN = "valid.jwt.token";
    private final UUID USER_ID = UUID.randomUUID();
    @Test
    void registerAndAuthenticate_Success() {
        UUID userId = UUID.randomUUID();
        UserSecurityDTO userSecurityDTO = new UserSecurityDTO();
        userSecurityDTO.setId(userId);
        String generatedAccessToken = "new.generated.token";
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(userService.findById(userId)).thenReturn(userSecurityDTO);
        when(jwtTokenProvider.generateAccessToken(userSecurityDTO)).thenReturn(generatedAccessToken);
        userAuthenticationService.registerAndAuthenticate(userId, response);
        verify(userService, times(1)).findById(userId);
        verify(jwtTokenProvider, times(1)).generateAccessToken(userSecurityDTO);
        verify(refreshTokenService, times(1)).createRefreshToken(eq(userSecurityDTO), anyString());
        verify(cookieService, times(1)).updateAuthCookies(response, generatedAccessToken);
    }
    @Test
    void getAuthenticatedUser_Success() throws JwtAuthenticationException {
        UserSecurityDTO mockUser = new UserSecurityDTO();
        mockUser.setId(USER_ID);

        when(cookieService.extractCookieJWT(request, "access_token")).thenReturn(VALID_TOKEN);
        when(jwtTokenProvider.getUserIdFromToken(VALID_TOKEN)).thenReturn(USER_ID);
        when(userService.findById(USER_ID)).thenReturn(mockUser);
        UserSecurityDTO result = userAuthenticationService.getAuthenticatedUser(request);
        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        verify(userService, times(1)).findById(USER_ID);
    }

    @Test
    void getAuthenticatedUser_MissingToken_ThrowsException() {
        when(cookieService.extractCookieJWT(request, "access_token")).thenReturn(null);
        assertThrows(AuthenticationTokenMissingException.class, () ->
                userAuthenticationService.getAuthenticatedUser(request)
        );
    }

    @Test
    void getAuthenticatedUser_InvalidToken_ThrowsException() throws JwtAuthenticationException {
        when(cookieService.extractCookieJWT(request, "access_token")).thenReturn("invalid.token");
        when(jwtTokenProvider.getUserIdFromToken("invalid.token"))
                .thenThrow(new JwtAuthenticationException("access token expired"));

        assertThrows(AuthenticationTokenMissingException.class, () ->
                userAuthenticationService.getAuthenticatedUser(request)
        );
    }

    @Test
    void getAuthenticatedUser_UserNotFound_ThrowsException() throws JwtAuthenticationException {
        when(cookieService.extractCookieJWT(request, "access_token")).thenReturn(VALID_TOKEN);
        when(jwtTokenProvider.getUserIdFromToken(VALID_TOKEN)).thenReturn(USER_ID);
        when(userService.findById(USER_ID)).thenReturn(null);
        assertThrows(AuthenticationTokenMissingException.class, () ->
                userAuthenticationService.getAuthenticatedUser(request)
        );
    }
    @Test
    void updateRefreshAccessToken_Success() {
        UserSecurityDTO userSecurityDTO = new UserSecurityDTO();
        userSecurityDTO.setId(USER_ID);
        String generatedAccessToken = "new.access.token";
        when(jwtTokenProvider.generateAccessToken(userSecurityDTO)).thenReturn(generatedAccessToken);
        HttpServletResponse response = mock(HttpServletResponse.class);
        userAuthenticationService.updateRefreshAccessToken(response, userSecurityDTO);
        verify(refreshTokenService, times(1)).createRefreshToken(eq(userSecurityDTO), anyString());
        verify(cookieService, times(1)).updateAuthCookies(response, generatedAccessToken);
    }
}