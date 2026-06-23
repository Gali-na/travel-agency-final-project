package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.security.JwtProvider;
import com.epam.travel_agency_final_project.service.СookieServiсe;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JwtProvider jwtProvider;

    private final СookieServiсe cookieServiсe;
    @GetMapping("/")
    public String index(HttpServletRequest request, Model model) {
        String accessToken = cookieServiсe.extractCookie(request, "access_token");

        if (accessToken != null && jwtProvider.validateAccessToken(accessToken)) {
            List<String> roles = jwtProvider.getRolesFromToken(accessToken);

            boolean isAdmin = roles.contains("ROLE_ADMIN");
            boolean isManager = roles.contains("ROLE_MANAGER") || isAdmin;

            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isManager", isManager);
        } else {
            model.addAttribute("isAdmin", false);
            model.addAttribute("isManager", false);
        }

        return "index";
    }

}