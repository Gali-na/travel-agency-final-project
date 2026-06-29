package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.dto.RefreshTokenDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.entity.RoleEntity;
import com.epam.travel_agency_final_project.entity.User;
import com.epam.travel_agency_final_project.exeption.AuthenticationTokenMissingException;
import com.epam.travel_agency_final_project.model.Role;
import com.epam.travel_agency_final_project.repository.RoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import com.epam.travel_agency_final_project.security.JwtProvider;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService {
    private final CookieService cookieService;
    private final JwtProvider jwtTokenProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final RoleRepository roleRepository;
    private static final Logger log = LogManager.getLogger(UserAuthenticationService.class);

    public UserSecurityDTO getAuthenticatedUser(HttpServletRequest request) {
        String refreshUUID = cookieService.extractCookie(request, "refresh_token");
        if (refreshUUID== null) {
            log.warn("Authentication failed: refresh_token cookie is missing.");
            throw new AuthenticationTokenMissingException("Access token is missing");
        }
        RefreshTokenDTO refreshTokenDTO = refreshTokenService.getRefreshToken(refreshUUID);

        if(refreshTokenDTO ==null){
            log.warn("Authentication failed: Refresh token not found in database for UUID: {}",
                    refreshUUID);
            throw new AuthenticationTokenMissingException("Access token is missing");
        }
        UserSecurityDTO userSecurityDTO = userService.findById(refreshTokenDTO.getUserId());
        if (userSecurityDTO == null) {
            log.error("Security inconsistency: Refresh token exists for UUID {}, but user {} not found!",
                    refreshUUID, refreshTokenDTO.getUserId());
            throw new AuthenticationTokenMissingException("Access token is invalid or user not found");
        }
        log.info("User {} successfully authenticated via refresh token.", userSecurityDTO.getId());
        return userSecurityDTO;
    }

    public void updateRefreshAccessToken(HttpServletResponse resp, UserSecurityDTO userSecurityDTO){
        log.debug("Updating access and refresh tokens for user: {}", userSecurityDTO.getId());
        String accessToken = jwtTokenProvider.generateAccessToken(userSecurityDTO);
        String refreshToken = UUID.randomUUID().toString();
        refreshTokenService.createRefreshToken(userSecurityDTO, refreshToken);
        cookieService.updateAuthCookies(resp, accessToken, refreshToken);
    }
    public void registerAndAuthenticate(UUID userId,HttpServletResponse resp) {
        log.debug("registered and authenticate: {}", userId);
        UserSecurityDTO userSecurityDTO = userService.findById(userId);
        updateRefreshAccessToken( resp, userSecurityDTO);
    }
}
