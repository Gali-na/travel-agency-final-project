package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.controller.TourWebController;
import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.model.Cart;
import com.epam.travel_agency_final_project.service.CookieService;
import com.epam.travel_agency_final_project.service.TourService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourWebControllerTest {
    private TourWebController controller;
    @Mock
    private TourService tourService;
    @Mock
    private CookieService cookieService;
    @Mock
    private Model model;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Page<TourFullDTO> page;

    @BeforeEach
    void setUp() {
        controller = new TourWebController(tourService, cookieService);
    }
    @Test
    void getToursPage_ReturnsToursView() {
        when(tourService.getTours(anyString(), any(), anyInt(), anyInt())).thenReturn(page);
        when(page.getContent()).thenReturn(List.of(new TourFullDTO()));

        String view = controller.getToursPage("uk", 0, null, null, null, model);

        assertEquals("tours", view);
        verify(model).addAttribute(eq("tours"), anyList());
        verify(model).addAttribute(eq("currentPage"), eq(0));
    }
    @Test
    void addToCart_UpdatesCookieAndRedirects() {
        UUID tourId = UUID.randomUUID();
        Cart cart = new Cart();
        when(cookieService.getCartFromCookie(request)).thenReturn(cart);

        String view = controller.addToCart(tourId, "uk", request, response);

        assertEquals("redirect:/tours?lang=uk", view);
        verify(response, times(1)).addCookie(any());
    }
}