package com.epam.travel_agency_final_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoController {

    /**
     * Відображає сторінку з повідомленням про блокування.
     * Доступна за адресою: /blocked
     */
    @GetMapping("/blocked")
    public String showBlockedPage() {
        return "blocked"; // Вказує на файл blocked.html у папці templates
    }

    @GetMapping("/tour-not-found")
    public String showTourNotFoundPage() {
        return "tour-not-found"; // назва вашого HTML файлу
    }
    @GetMapping("/checkoutInfo")
    public String showCheckoutInfoPage() {
        // Повертає назву вашого HTML-файлу (checkoutInfo.html)
        return "checkoutInfo";
    }
    @GetMapping("/admin/invalid-email")
    public String showInvalidEmailPage() {
        // Повертає назву вашого HTML-файлу (checkoutInfo.html)
        return "checkoutInfo";
    }

    @GetMapping("/admin/tour-createdInfo")
    public String showTourCreatedPage() {
        // Повертає назву вашого HTML-файлу (checkoutInfo.html)
        return "admin/tour-createdInfo";
    }


    @GetMapping("/checkout-success")
    public String checkoutSuccess() {
        return "checkout-success";
    }
}