package com.epam.travel_agency_final_project.security;
import com.epam.travel_agency_final_project.dto.RefreshTokenDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.mapper.UserSecurityMapper;
import com.epam.travel_agency_final_project.service.RefreshTokenService;
import com.epam.travel_agency_final_project.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class JwtCookieFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final UserSecurityMapper userSecurityMapper;
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
        String refreshUUID = extractCookie(request, "refresh_token");

        RefreshTokenDTO refreshCurrentToken=null;
        UserSecurityDTO userSecurityDTO=null;
        if (accessToken != null && jwtProvider.validateAccessToken(accessToken)&& refreshUUID != null) {
            refreshCurrentToken = refreshTokenService.getRefreshToken(refreshUUID);
            userSecurityDTO = userService.findById(refreshCurrentToken.getUserId());
            authenticateUserInContext(accessToken,userSecurityDTO);

        }

        if (accessToken != null && jwtProvider.validateAccessToken(accessToken)&&refreshUUID != null) {
            refreshCurrentToken = refreshTokenService.getRefreshToken(refreshUUID);
            userSecurityDTO = userService.findById(refreshCurrentToken.getUserId());
            authenticateUserInContext(accessToken,userSecurityDTO);
            filterChain.doFilter(request, response);
            return;
        }

        if (refreshUUID != null && jwtProvider.isTokenExpired(accessToken)) {
            refreshCurrentToken = refreshTokenService.getRefreshToken(refreshUUID);

            if (refreshCurrentToken != null) {
                userSecurityDTO = userService.findById(refreshCurrentToken.getUserId());
                if (userSecurityDTO == null) {
                    response.sendRedirect("/register");
                    return;
                }
                if (userSecurityDTO.isLocked()) {
                    response.sendRedirect("/blok");
                    return;
                }
                String rotatedToken = refreshTokenService.rotateRefreshToken(refreshUUID);

                if (rotatedToken != null) {
                    String newAccessToken = jwtProvider.generateAccessToken(userSecurityDTO);
                    updateAuthCookies(response, newAccessToken, rotatedToken);
                    authenticateUserInContext(newAccessToken, userSecurityDTO);
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
    private void authenticateUserInContext(String token, UserSecurityDTO userDTO) {
        String login = jwtProvider.getLoginFromToken(token);
        List<String> roles = userDTO.getRoles();

        var authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .toList();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(login, null, authorities)
        );
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void updateAuthCookies(HttpServletResponse response, String accessToken, String refreshUUID) {
        Cookie accessCookie = new Cookie("access_token", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(15 * 60);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refresh_token", refreshUUID);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(30 * 24 * 60 * 60);
        response.addCookie(refreshCookie);
    }
}