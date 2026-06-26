package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.controller.AdminTourController;
import com.epam.travel_agency_final_project.dto.TourCreationDTO;
import com.epam.travel_agency_final_project.service.CityService;
import com.epam.travel_agency_final_project.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminTourControllerTest {

    private AdminTourController controller;

    @Mock
    private CityService cityService;
    @Mock
    private TourService tourService;
    @Mock
    private Model model;
    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        controller = new AdminTourController(cityService, tourService);
    }

    @Test
    void createTour_ValidationErrors_ReturnsForm() {
        when(bindingResult.hasErrors()).thenReturn(true);
        TourCreationDTO dto = new TourCreationDTO();
        String viewName = controller.createTour(dto, bindingResult, model);
        assertEquals("admin/create-tour", viewName);
        verify(tourService, never()).createFullTour(any());
        verify(cityService, times(1)).findAll();
        verify(model, atLeastOnce()).addAttribute(anyString(), any());
    }

    @Test
    void createTour_SuccessfulCreation_PerformsRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);
        TourCreationDTO dto = new TourCreationDTO();

        String viewName = controller.createTour(dto, bindingResult, model);

        assertEquals("redirect:/admin/tour-createdInfo", viewName);
        verify(tourService, times(1)).createFullTour(dto);
        verify(cityService, never()).findAll();
    }
    @Test
    void showCreateTourForm_ReturnsViewAndAttributes() {
        String viewName = controller.showCreateTourForm(model);
        assertEquals("admin/create-tour", viewName);
        verify(model).addAttribute(eq("tourCreationDTO"), any(TourCreationDTO.class));
        verify(model).addAttribute(eq("cities"), any());
        verify(model).addAttribute(eq("tourTypes"), any());
        verify(model).addAttribute(eq("transferTypes"), any());
        verify(model).addAttribute(eq("hotelTypes"), any());
        verify(cityService, times(1)).findAll();
    }
}