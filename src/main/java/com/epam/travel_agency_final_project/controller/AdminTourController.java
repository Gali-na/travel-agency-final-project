package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.dto.TourCreationDTO;
import com.epam.travel_agency_final_project.entity.City;
import com.epam.travel_agency_final_project.model.HotelType;
import com.epam.travel_agency_final_project.model.TourType;
import com.epam.travel_agency_final_project.model.TransferType;
import com.epam.travel_agency_final_project.service.CityService;
import com.epam.travel_agency_final_project.service.TourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
    public class AdminTourController {
    private final CityService cityService;
    private final TourService tourService;
    @GetMapping("/admin/create-tour")
    public String showCreateTourForm(Model model) {

        model.addAttribute("tourCreationDTO", new TourCreationDTO());
        model.addAttribute("cities", cityService.findAll()); // Передаємо DTO в модель
// Передаємо списки міст та енумів

        model.addAttribute("tourTypes", TourType.values());
        model.addAttribute("transferTypes", TransferType.values());
        model.addAttribute("hotelTypes", HotelType.values());
        return "admin/create-tour";
    }


//    @GetMapping("/admin/create-tour")
//    public String showCreateForm(Model model) {
//        model.addAttribute("tourCreationDTO", new TourCreationDTO());
//        addAttributesToModel(model); // Наповнюємо модель перед відображенням
//        return "admin/create-tour";
//    }



    @PostMapping("/admin/tours/create")
    public String createTour(@Valid @ModelAttribute("tourCreationDTO") TourCreationDTO dto,
                             BindingResult bindingResult,
                             Model model) {


        System.out.println("***************************");

        System.out.println(dto.toString());

        System.out.println("***************************");
        if (bindingResult.hasErrors()) {
            addAttributesToModel(model); // <--- ТУТ ми повертаємо дані для списків, щоб форма не "впала"
            System.out.println("***************************");

            if (bindingResult.hasErrors()) {
                bindingResult.getAllErrors().forEach(error -> {
                    System.out.println("Помилка в полі: " + ((FieldError) error).getField());
                    System.out.println("Текст помилки: " + error.getDefaultMessage());
                });
            }

            System.out.println("***************************");

            return "admin/create-tour";
        }

        tourService.createFullTour(dto);
        return "redirect:/admin/tour-createdInfo";
    }
    private void addAttributesToModel(Model model) {
        model.addAttribute("cities", cityService.findAll()); // Приклад вашого сервісу міст
        model.addAttribute("tourTypes", TourType.values());   // Або список рядків
        model.addAttribute("transferTypes", TransferType.values());
        model.addAttribute("hotelTypes", HotelType.values());
    }
    }

