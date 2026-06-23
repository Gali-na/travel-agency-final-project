package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.dto.UserProfileDTO;
import com.epam.travel_agency_final_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;


@RequiredArgsConstructor
@Controller
public class AdminUserController {

private  final UserService userService;
@GetMapping("/admin/users")
    public String listUsers(Model model,
                            @PageableDefault(size = 5) Pageable pageable,
                            @RequestParam(required = false) String email) {

    System.out.println("*************************");
    System.out.println("Тип об'єкта в Page:  @GetMapping(\"/admin/users\") ") ;
    System.out.println("*************************");

        // 1. Якщо email прийшов (користувач натиснув "Пошук")
        if (email != null && !email.trim().isEmpty()) {
            Page<UserProfileDTO> result = userService.findByEmailExact(email, pageable);
            // Викидаємо виключення ТІЛЬКИ якщо результату немає

            System.out.println("*************************");
       //     System.out.println("Тип об'єкта в Page: " + result.getContent().get(0).getClass().getName());
            System.out.println("*************************");
            if (result.isEmpty()) {
                return "admin/invalid-email";
            }
            model.addAttribute("users", result);
            model.addAttribute("email", email);
        } else {
            // 2. Якщо email null — просто виводимо список усіх користувачів без помилок
            model.addAttribute("users", userService.findAll(pageable));
        }

        return "admin/users";
}

    @PostMapping("/admin/users/lock/{id}")
    public String lockUser(@PathVariable("id") UUID id) {
        // Викликаємо сервіс для зміни статусу
          userService.lockUser(id);

        // Після успішної дії перенаправляємо назад на список
        return "redirect:/admin/users";
    }
}