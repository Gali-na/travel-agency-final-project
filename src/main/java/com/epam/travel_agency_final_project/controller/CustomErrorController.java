package com.epam.travel_agency_final_project.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;
@Slf4j
@RequiredArgsConstructor
@Controller
public class CustomErrorController implements ErrorController {
    private final MessageSource messageSource;
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model, Locale locale){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = (status != null) ? Integer.parseInt(status.toString()) : 500;
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String titleKey = (statusCode >= 400 && statusCode < 500) ? "error.client.title" : "error.server.title";
        String messageKey = (statusCode >= 400 && statusCode < 500) ? "error.client.message" : "error.server.message";
        String title = messageSource.getMessage(titleKey, null, locale);
        String message = messageSource.getMessage(messageKey, null, locale);
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("title", title);
        model.addAttribute("message", message);
        log.error("Error occurred! Status Code: {}. Request URI: {}", statusCode, requestUri);
        return "admin/error_page";
    }
}
