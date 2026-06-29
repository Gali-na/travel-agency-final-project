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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Locale;
@Slf4j
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
        log.info("Attempting to create a new tour: {}", dto.getTitleEn());
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors occurred during tour creation: {}", bindingResult.getErrorCount());
            addAttributesToModel(model);
            return "admin/create-tour";
        }
        try {
            dto.validate();
        } catch (ValidationException e) {
            log.error("Custom validation failed for tour: {}", e.getMessage());
            String errorMessage = messageSource.getMessage(e.getMessage(), null, locale);
            bindingResult.reject(null, errorMessage);
            addAttributesToModel(model);
            return "admin/create-tour";
        }
        tourService.createFullTour(dto);
        log.info("Tour successfully created: {}", dto.getTitleEn());
        return "admin/tour-createdInfo";
    }
    private void addAttributesToModel(Model model) {
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("tourTypes", TourType.values());
        model.addAttribute("transferTypes", TransferType.values());
        model.addAttribute("hotelTypes", HotelType.values());
    }
}

