package com.epam.travel_agency_final_project.controller;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.epam.travel_agency_final_project.dto.TourCreationDTO;
import com.epam.travel_agency_final_project.service.CityService;
import com.epam.travel_agency_final_project.service.TourService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.boot.autoconfigure.AutoConfigurationPackages.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminTourControllerTest {
    @Mock
    private CityService cityService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private TourService tourService;
    private MockMvc mockMvc;
    private AdminTourController adminTourController;

    @BeforeEach
    void setUp() {
        adminTourController = new AdminTourController(cityService, tourService, messageSource);
        mockMvc = MockMvcBuilders.standaloneSetup(adminTourController).build();
    }
    @Test
    void showCreateTourForm() throws Exception {
        mockMvc.perform(get("/admin/create-tour"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/create-tour"))
                .andExpect(model().attributeExists("tourCreationDTO", "cities", "tourTypes", "transferTypes", "hotelTypes"));

        verify(cityService).findAll();
    }
    @Test
    void createTour_Success() throws Exception {
        mockMvc.perform(post("/admin/tours/create")
                        .param("titleEn", "Valid Title")
                        .param("titleUa", "Валідна назва")
                        .param("descriptionEn", "Description")
                        .param("descriptionUa", "Опис")
                        .param("price", "100")
                        .param("arrivalDate", "2026-07-01T10:00:00")
                        .param("evictionDate", "2026-07-10T10:00:00")
                        .param("isHot", "false")
                        .param("imagePath", "image.jpg")
                        .param("tourType", "CULTURAL")
                        .param("transferType", "PLANE")
                        .param("hotelType", "FIVE_STARS")
                        .param("cityId", "550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tour-createdInfo"));

        verify(tourService).createFullTour(any(TourCreationDTO.class));
    }

    @Test
    void createTour_BindingErrors() throws Exception {
        mockMvc.perform(post("/admin/tours/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/create-tour"));

        verify(tourService, never()).createFullTour(any());
        verify(cityService).findAll();
    }
    @Test
    public void createTour_CustomValidationException() throws Exception {
        TourCreationDTO dto = spy(new TourCreationDTO());
        doThrow(new ValidationException("some.error.key")).when(dto).validate();
        when(messageSource.getMessage(eq("some.error.key"), any(), any(Locale.class)))
                .thenReturn("Validation Error Message");
        mockMvc.perform(post("/admin/tours/create")
                        .flashAttr("tourCreationDTO", dto)
                        // Додаємо валідні дані, щоб пройти @Valid, але впасти на dto.validate()
                        .param("titleEn", "Valid")
                        .param("titleUa", "Валідна")
                        .param("descriptionEn", "Valid")
                        .param("descriptionUa", "Валідна")
                        .param("price", "100")
                        .param("arrivalDate", "2026-07-01T10:00:00")
                        .param("evictionDate", "2026-07-10T10:00:00")
                        .param("isHot", "false")
                        .param("imagePath", "path")
                        .param("tourType", "CULTURAL")
                        .param("transferType", "PLANE")
                        .param("hotelType", "FIVE_STARS")
                        .param("cityId", "550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/create-tour"));

        verify(tourService, never()).createFullTour(any());
    }
}
