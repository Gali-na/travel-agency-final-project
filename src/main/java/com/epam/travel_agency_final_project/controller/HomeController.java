package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final JwtProvider jwtProvider;
    private final CookieService cookieServiсe;
    @GetMapping("/")
    public String index(HttpServletRequest request, Model model) {
        String accessToken = cookieServiсe.extractCookie(request, "access_token");
        if (accessToken != null && jwtProvider.validateAccessToken(accessToken)) {
            log.debug("User token present and valid.");
            List<String> roles = jwtProvider.getRolesFromToken(accessToken);
            boolean isAdmin = roles.contains("ROLE_ADMIN");
            boolean isManager = roles.contains("ROLE_MANAGER") || isAdmin;
            log.debug("Access roles determined: isAdmin={}, isManager={}", isAdmin, isManager);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isManager", isManager);
        } else {
            log.debug("No valid access token found. Guest user access.");
            model.addAttribute("isAdmin", false);
            model.addAttribute("isManager", false);
        }
        return "index";
    }
}