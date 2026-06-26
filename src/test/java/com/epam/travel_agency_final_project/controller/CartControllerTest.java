package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.controller.CartController;
import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.model.Cart;
import com.epam.travel_agency_final_project.service.TourService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    private CartController controller;

    @Mock private TourService tourService;
    @Mock private HttpServletRequest request;
    @Mock private Model model;

    @BeforeEach
    void setUp() {
        controller = new CartController(tourService);
    }

    @Test
    void showCart_EmptyCart_ReturnsView() {
        Cart emptyCart = new Cart();
        String json = URLEncoder.encode(emptyCart.toJson(), StandardCharsets.UTF_8);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("cart", json)});

        String view = controller.showCart(request, model, "uk");

        assertEquals("cart", view);
        verify(model).addAttribute(eq("cartTours"), eq(Collections.emptyList()));
        verify(tourService, never()).getToursForCart(any(), anyString());
    }

    @Test
    void showCart_WithItems_ReturnsViewWithTours() {
        UUID tourId = UUID.randomUUID();
        Cart cart = new Cart();
        cart.addTour(tourId);

        String json = URLEncoder.encode(cart.toJson(), StandardCharsets.UTF_8);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("cart", json)});

        TourFullDTO tourDto = new TourFullDTO();
        tourDto.setId(tourId);

        when(tourService.getToursForCart(anySet(), eq("uk"))).thenReturn(List.of(tourDto));

        String view = controller.showCart(request, model, "uk");

        assertEquals("cart", view);
        verify(model).addAttribute(eq("cartTours"), argThat(list -> ((List<?>)list).size() == 1));
        assertEquals(1, tourDto.getQuantity());
    }
}