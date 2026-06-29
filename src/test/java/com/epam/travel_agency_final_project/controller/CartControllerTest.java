package com.epam.travel_agency_final_project.controller;

import com.epam.travel_agency_final_project.controller.CartController;
import com.epam.travel_agency_final_project.dto.TourDTO;
import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.model.Cart;
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
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.boot.autoconfigure.AutoConfigurationPackages.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {
    @Mock
    private CookieService cookieService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private TourService tourService;
    @Mock
    private Model model;
    @InjectMocks
    private CartController cartController;
    private UUID tourId;
    @BeforeEach
    void setUp() {
        tourId = UUID.randomUUID();
    }
    @Test
        void testTourDTOFields() {
            UUID cityId = UUID.randomUUID();
            BigDecimal price = new BigDecimal("1000.00");
            LocalDateTime arrival = LocalDateTime.now();
            LocalDateTime eviction = LocalDateTime.now().plusDays(7);

            TourDTO dto = TourDTO.builder()
                    .cityId(cityId)
                    .price(price)
                    .arrivalDate(arrival)
                    .evictionDate(eviction)
                    .isHot(true)
                    .imagePath("path/to/image.jpg")
                    .build();

            assertEquals(cityId, dto.getCityId());
            assertEquals(price, dto.getPrice());
            assertEquals(arrival, dto.getArrivalDate());
            assertEquals(eviction, dto.getEvictionDate());
            assertTrue(dto.getIsHot());
            assertEquals("path/to/image.jpg", dto.getImagePath());
        }

        @Test
        void testSetters() {
            TourDTO dto = new TourDTO();
            dto.setPrice(new BigDecimal("500.00"));

            assertEquals(new BigDecimal("500.00"), dto.getPrice());
        }

    @Test
    void showCart_EmptyCart_ShouldReturnEmptyList() {
        when(cookieService.parseCookieDecoder(any())).thenReturn("{\"items\":{}}");
        String viewName = cartController.showCart(request, model, "en");
        assertEquals("cart", viewName);
        verify(tourService, never()).getToursForCart(anySet(), anyString());
        verify(model).addAttribute(eq("cartTours"), anyList());
    }
    @Test
    void removeFromCart_ShouldRemoveTourAndAddCookie() {
        UUID tourId = UUID.randomUUID();
        String lang = "uk";
        Cart cart = mock(Cart.class);

        try (MockedStatic<Cart> cartMock = mockStatic(Cart.class)) {
            cartMock.when(() -> Cart.fromJson(anyString())).thenReturn(cart);
            when(cookieService.parseCookieDecoder(any())).thenReturn("someJson");
            when(cart.toJson()).thenReturn("updatedJson");
            String view = cartController.removeFromCart(tourId, lang, request, response);

            assertEquals("redirect:/cart?lang=uk", view);
            verify(cart).removeTour(tourId);

            ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
            verify(response).addCookie(cookieCaptor.capture());

            Cookie addedCookie = cookieCaptor.getValue();
            assertEquals("cart", addedCookie.getName());
            assertEquals(30 * 60, addedCookie.getMaxAge());
            assertEquals("/", addedCookie.getPath());
        }
    }

    @Test
    void showCart_WhenCartIsNotEmpty_ShouldCallTourServiceAndSetQuantity() {
        String lang = "uk";
        UUID tourId = UUID.randomUUID();

        Cart cart = new Cart();
        cart.getItems().put(tourId, 3);

        try (MockedStatic<Cart> cartMock = mockStatic(Cart.class)) {
            cartMock.when(() -> Cart.fromJson(anyString())).thenReturn(cart);
            when(cookieService.parseCookieDecoder(any())).thenReturn("someJson");

            TourFullDTO tourDTO = new TourFullDTO();
            tourDTO.setId(tourId);
            List<TourFullDTO> tourList = new ArrayList<>(Collections.singletonList(tourDTO));
            when(tourService.getToursForCart(anySet(), eq(lang))).thenReturn(tourList);
            cartController.showCart(request, model, lang);
            verify(tourService, times(1)).getToursForCart(argThat(ids -> ids.contains(tourId)), eq(lang));
            assertEquals(3, tourDTO.getQuantity());
            verify(model).addAttribute("cartTours", tourList);
        }
    }
}