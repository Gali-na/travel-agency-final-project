package com.epam.travel_agency_final_project.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class InfoController {
    @GetMapping("/blocked")
    public String showBlockedPage() {
        log.info("User accessed the blocked account page.");
        return "blocked";
    }
    @GetMapping("/tour-not-found")
    public String showTourNotFoundPage() {
        log.warn("User accessed the tour-not-found page.");
        return "tour-not-found";
    }
    @GetMapping("/checkoutInfo")
    public String showCheckoutInfoPage() {
        log.info("User accessed checkout info page.");
        return "checkoutInfo";
    }
    @GetMapping("/admin/invalid-email")
    public String showInvalidEmailPage() {
        log.warn("Admin accessed the invalid-email warning page.");
        return "/admin/invalid-email";
    }
    @GetMapping("/admin/tour-createdInfo")
    public String showTourCreatedPage() {
        log.info("Admin accessed the tour-created confirmation page.");
        return "admin/tour-createdInfo";
    }
    @GetMapping("/checkout-success")
    public String checkoutSuccess() {
        log.info("User accessed the checkout-success page.");
        return "checkout-success";
    }
}