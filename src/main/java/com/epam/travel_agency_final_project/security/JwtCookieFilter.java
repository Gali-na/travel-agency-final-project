package com.epam.travel_agency_final_project.security;

import com.epam.travel_agency_final_project.aspect.controller.logging.AdminTourControllerCreateTourLogging;
import com.epam.travel_agency_final_project.dto.RefreshTokenDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.service.CookieService;
import com.epam.travel_agency_final_project.service.RefreshTokenService;
import com.epam.travel_agency_final_project.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class JwtCookieFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final CookieService cookieService;
    private static final Logger logger = LogManager.getLogger(JwtCookieFilter.class);

    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/")
                || path.equals("/index")
                || path.equals("/login")
                || path.equals("/register")
                || path.startsWith("/tours")
                || path.startsWith("/cart")
                || path.startsWith("/css/")
                || path.startsWith("/blocked/")
                || path.startsWith("/js/")
                || path.startsWith("/uploads/");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = cookieService.extractCookie(request, "access_token");
        String refreshUUID = cookieService.extractCookie(request, "refresh_token");

        if (isAccessTokenValid(accessToken, refreshUUID)) {
            filterChain.doFilter(request, response);
            logger.info(" access and refresh token  are valid");
            return;
        }
        if (refreshUUID != null && jwtProvider.isTokenExpired(accessToken)) {
            if (handleTokenRefresh(refreshUUID, response)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAccessTokenValid(String accessToken, String refreshUUID) {
        if (accessToken != null && jwtProvider.validateAccessToken(accessToken) && refreshUUID != null) {
            RefreshTokenDTO token = refreshTokenService.getRefreshToken(refreshUUID);
            UserSecurityDTO user = (token != null) ? userService.findById(token.getUserId()) : null;
            if (user != null) {
                authenticateUserInContext(accessToken, user);
                return true;
            }
        }
        return false;
    }

    private boolean handleTokenRefresh(String refreshUUID, HttpServletResponse response) throws IOException {
        RefreshTokenDTO refreshToken = refreshTokenService.getRefreshToken(refreshUUID);
        if (refreshToken == null) {
            logger.warn("Refresh token not found in database for UUID: {}", refreshUUID);
            return false;
        }
        UserSecurityDTO user = userService.findById(refreshToken.getUserId());
        if (user == null) {
            response.sendRedirect("/register");
            logger.error("User not found for ID: from token {}", refreshToken.getUserId());
            return true;
        }
        if (user.isLocked()) {
            logger.error("User is blok  userId{}", user.getId());
            response.sendRedirect("/blok");
            return true;
        }
        String rotatedToken = refreshTokenService.rotateRefreshToken(refreshUUID);
        if (rotatedToken != null) {
            String newAccessToken = jwtProvider.generateAccessToken(user);
            cookieService.updateAuthCookies(response, newAccessToken,rotatedToken);
            authenticateUserInContext(newAccessToken, user);
            logger.error("User '{}' successfully authenticated  Access Toke", user.getId());
            return true;
        }
        return false;
    }
    private void authenticateUserInContext(String token, UserSecurityDTO userDTO) {
        String login = jwtProvider.getLoginFromToken(token);
        List<String> roles = userDTO.getRoles();

        var authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .toList();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(login, null, authorities));
        logger.error("User '{}' successfully authenticated  in   SecurityContext", userDTO.getId());
    }
}