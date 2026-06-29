package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.dto.TourCreationDTO;
import com.epam.travel_agency_final_project.model.HotelType;
import com.epam.travel_agency_final_project.model.TourType;
import com.epam.travel_agency_final_project.model.TransferType;
import com.epam.travel_agency_final_project.service.CityService;
import com.epam.travel_agency_final_project.service.TourService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Locale;

@RequiredArgsConstructor
@Controller
public class AdminTourController {
    private final CityService cityService;
    private final TourService tourService;

    private final MessageSource messageSource;
    @GetMapping("/admin/create-tour")
    public String showCreateTourForm(Model model) {
        model.addAttribute("tourCreationDTO", new TourCreationDTO());
        addAttributesToModel(model);
        return "admin/create-tour";
    }
    @PostMapping("/admin/tours/create")
    public String createTour(@Valid @ModelAttribute("tourCreationDTO") TourCreationDTO dto,
                             BindingResult bindingResult,
                             Model model, Locale locale) {
        if (bindingResult.hasErrors()) {
            addAttributesToModel(model);
            return "admin/create-tour";
        }
        try {
            dto.validate();
        } catch (ValidationException e) {
            String errorMessage = messageSource.getMessage(e.getMessage(), null, locale);
            bindingResult.reject(null, errorMessage);
            addAttributesToModel(model);
            return "admin/create-tour";
        }
        tourService.createFullTour(dto);
        return "redirect:/admin/tour-createdInfo";
    }
    private void addAttributesToModel(Model model) {
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("tourTypes", TourType.values());
        model.addAttribute("transferTypes", TransferType.values());
        model.addAttribute("hotelTypes", HotelType.values());
    }
}

