package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.dto.UserRegistrationDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.exeption.AuthenticationTokenMissingException;
import com.epam.travel_agency_final_project.exeption.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.dto.UserRegistrationDTO;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService {
    private final CookieService cookieService;
    private final JwtProvider jwtTokenProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    public UserSecurityDTO getAuthenticatedUser(HttpServletRequest request) {
        String accessToken = cookieService.extractCookieJWT(request, "access_token");
        if (accessToken == null) {
            throw new AuthenticationTokenMissingException("Access token is missing");
        }
        UUID userId = null;
        try {
            userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        } catch (JwtAuthenticationException e) {
            throw new AuthenticationTokenMissingException("Access token is invalid or user not found");
        }

        UserSecurityDTO userSecurityDTO = userService.findById(userId);
        if (userSecurityDTO == null) {
            throw new AuthenticationTokenMissingException("Access token is invalid or user not found");
        }
        return userSecurityDTO;
    }

    public void updateRefreshAccessToken(HttpServletResponse resp, UserSecurityDTO userSecurityDTO){
        String accessToken = jwtTokenProvider.generateAccessToken(userSecurityDTO);
        String refreshToken = UUID.randomUUID().toString();
        refreshTokenService.createRefreshToken(userSecurityDTO, refreshToken);
       // cookieService.updateAuthCookies(resp, accessToken, refreshToken);
        cookieService.updateAuthCookies(resp, accessToken);
    }
    public void registerAndAuthenticate(UUID userId,HttpServletResponse resp) {
        UserSecurityDTO userSecurityDTO = userService.findById(userId);
        String accessToken = jwtTokenProvider.generateAccessToken(userSecurityDTO);
        String refreshToken = UUID.randomUUID().toString();
        refreshTokenService.createRefreshToken(userSecurityDTO, refreshToken);
       // cookieService.updateAuthCookies(resp, accessToken, refreshToken);
        cookieService.updateAuthCookies(resp, accessToken);
    }
}
