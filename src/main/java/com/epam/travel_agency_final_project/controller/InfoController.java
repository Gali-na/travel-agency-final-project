package com.epam.travel_agency_final_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoController {
    @GetMapping("/blocked")
    public String showBlockedPage() {
        return "blocked";
    }

    @GetMapping("/tour-not-found")
    public String showTourNotFoundPage() {
        return "tour-not-found";
    }
    @GetMapping("/checkoutInfo")
    public String showCheckoutInfoPage() {
        return "checkoutInfo";
    }
    @GetMapping("/admin/invalid-email")
    public String showInvalidEmailPage() {
        return "checkoutInfo";
    }
    @GetMapping("/admin/tour-createdInfo")
    public String showTourCreatedPage() {
        return "admin/tour-createdInfo";
    }
    @GetMapping("/checkout-success")
    public String checkoutSuccess() {
        return "checkout-success";
    }
}