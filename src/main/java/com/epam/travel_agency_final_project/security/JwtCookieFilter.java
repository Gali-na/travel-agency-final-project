package com.epam.travel_agency_final_project.security;

import com.epam.travel_agency_final_project.aspect.controller.logging.AdminTourControllerCreateTourLogging;
import com.epam.travel_agency_final_project.dto.RefreshTokenDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.entity.RefreshToken;
import com.epam.travel_agency_final_project.exeption.JwtAuthenticationException;
import com.epam.travel_agency_final_project.mapper.UserSecurityMapper;
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
    private final UserSecurityMapper userSecurityMapper;
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

        String accessToken = extractCookie(request, "access_token");

        try {
            if (accessToken != null && jwtProvider.validateAccessToken(accessToken)) {
                UUID userId = jwtProvider.getUserIdFromToken(accessToken);
                UserSecurityDTO user = userService.findById(userId);
                if (user == null) {
                    logger.info("User {} not found", userId);
                    response.sendRedirect("/login");
                    return;
                }
                if (user.isLocked()) {
                    logger.info("User {} is blocked", userId);
                    response.sendRedirect("/blok");
                    return;
                }

                RefreshTokenDTO refreshTokenDto = refreshTokenService.getRefreshTokenByUserId(userId);
                if (refreshTokenDto == null) {
                    logger.error("No refresh token for user {}", userId);
                    response.sendRedirect("/login");
                    return;
                }

                String newRefreshToken = refreshTokenService.rotateRefreshToken(refreshTokenDto.getToken());
                if (newRefreshToken == null) {
                    logger.warn("Failed to rotate token for user {}", userId);
                    response.sendRedirect("/login");
                    return;
                }

                String newAccessToken = jwtProvider.generateAccessToken(user);
                cookieService.updateAuthCookies(response, newAccessToken);
                authenticateUserInContext(newAccessToken, user);
                logger.info("user: {}  authenticated in context", userId);
                filterChain.doFilter(request, response);
                return;
            }
        } catch (JwtAuthenticationException e) {
            logger.error("Auth error: {}", e.getMessage());
            response.sendRedirect("/login");
            return;
        }

        filterChain.doFilter(request, response);
    }
    private void authenticateUserInContext(String token, UserSecurityDTO userDTO) {
        String login = "";
        try {
            login = jwtProvider.getLoginFromToken(token);
        } catch (JwtAuthenticationException e) {
            logger.error("invalid access toke");
        }

        List<String> roles = userDTO.getRoles();
        var authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .toList();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(login, null, authorities));
        logger.error("User '{}' successfully authenticated  in   SecurityContext", userDTO.getId());
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}