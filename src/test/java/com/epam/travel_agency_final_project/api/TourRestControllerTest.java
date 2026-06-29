package com.epam.travel_agency_final_project.api;

import com.epam.travel_agency_final_project.api.TourRestController;
import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.model.Cart;
import com.epam.travel_agency_final_project.model.TourFilter;
import com.epam.travel_agency_final_project.service.CookieService;
import com.epam.travel_agency_final_project.service.TourService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TourRestControllerTest {
    @Mock
    private TourService tourService;
    @Mock
    private CookieService cookieService;
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TourRestController tourRestController;

    private Page<TourFullDTO> mockPage;

    @BeforeEach
    void setUp() {
        mockPage = new PageImpl<>(Collections.singletonList(new TourFullDTO()));
    }

    @Test
    void getAllTours_ShouldReturnPageOfTours() {
        String lang = "uk";
        int page = 0;
        int size = 2;
        Boolean isHot = true;
        String tourType = "SPORT";
        String hotelType = null;
        when(tourService.getTours(eq(lang), any(TourFilter.class), eq(page), eq(size)))
                .thenReturn(mockPage);

        ResponseEntity<Page<TourFullDTO>> response = tourRestController.getAllTours(
                lang, page, size, isHot, tourType, hotelType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPage, response.getBody());
    }
    @Test
    void addToCart_ShouldUpdateCartAndAddCookie() {
        UUID tourId = UUID.randomUUID();
        Cart cart = new Cart();

        when(cookieService.getCartFromCookie(request)).thenReturn(cart);

        ResponseEntity<Cart> responseEntity = tourRestController.addToCart(tourId, request, response);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(cart, responseEntity.getBody());

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());

        Cookie capturedCookie = cookieCaptor.getValue();
        assertEquals("cart", capturedCookie.getName());
        assertEquals(1800, capturedCookie.getMaxAge());
    }
}