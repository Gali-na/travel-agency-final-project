package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.exeption.TourNotFoundException;
import com.epam.travel_agency_final_project.model.Cart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CookieServiceTest {
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private CookieService cookieService;
    @Test
    void updateAuthCookies_ShouldCoverAllLines() {
        String accessToken = "at";
        String refreshUUID = "rt";

        cookieService.updateAuthCookies(response, accessToken, refreshUUID);

        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(2)).addCookie(captor.capture());

        List<Cookie> cookies = captor.getAllValues();
        Cookie access = cookies.get(0);
        assertEquals("access_token", access.getName());
        assertEquals(accessToken, access.getValue());
        assertTrue(access.isHttpOnly());
        assertEquals("/", access.getPath());
        assertEquals(900, access.getMaxAge());

        Cookie refresh = cookies.get(1);
        assertEquals("refresh_token", refresh.getName());
        assertEquals(refreshUUID, refresh.getValue());
        assertTrue(refresh.isHttpOnly());
        assertEquals("/", refresh.getPath());
        assertEquals(2592000, refresh.getMaxAge());
    }
    @Test
    void updateCartCookieAfterPurchase_ShouldUpdateCookie_WhenTourExistsInCart() {
        UUID tourId = UUID.randomUUID();
        String initialJson = "{\"tours\":[{\"id\":\"" + tourId + "\"},{\"id\":\"" + UUID.randomUUID() + "\"}]}";
        String encodedInitial = URLEncoder.encode(initialJson, StandardCharsets.UTF_8);
        Cookie cartCookie = new Cookie("cart", encodedInitial);

        when(request.getCookies()).thenReturn(new Cookie[]{cartCookie});

        boolean result = cookieService.updateCartCookieAfterPurchase(tourId, request, response);

        assertTrue(result);
        verify(response).addCookie(argThat(c ->
                c.getName().equals("cart") && c.getMaxAge() == 1800
        ));
    }

    @Test
    void updateCartCookieAfterPurchase_ShouldThrowException_WhenNoCookies() {
        when(request.getCookies()).thenReturn(null);

        assertThrows(TourNotFoundException.class, () ->
                cookieService.updateCartCookieAfterPurchase(UUID.randomUUID(), request, response)
        );
    }

    @Test
    void updateCartCookieAfterPurchase_ShouldThrowException_WhenCartCookieMissing() {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("other", "val")});

        assertThrows(TourNotFoundException.class, () ->
                cookieService.updateCartCookieAfterPurchase(UUID.randomUUID(), request, response)
        );
    }
        @Test
    void parseCookieDecoder_ShouldReturnDecodedValue_WhenCartCookieExists() {
        String originalValue = "{\"id\":1, \"name\":\"item\"}";
        String encodedValue = URLEncoder.encode(originalValue, StandardCharsets.UTF_8);
        Cookie[] cookies = { new Cookie("other", "data"), new Cookie("cart", encodedValue) };

        String result = cookieService.parseCookieDecoder(cookies);

        assertEquals(originalValue, result);
    }

    @Test
    void parseCookieDecoder_ShouldReturnEmptyString_WhenCookiesArrayIsNull() {
        String result = cookieService.parseCookieDecoder(null);

        assertEquals("", result);
    }

    @Test
    void parseCookieDecoder_ShouldReturnEmptyString_WhenCartCookieNotFound() {
        Cookie[] cookies = { new Cookie("theme", "dark"), new Cookie("session", "123") };

        String result = cookieService.parseCookieDecoder(cookies);

        assertEquals("", result);
    }

    @Test
    void parseCookieDecoder_ShouldHandleEmptyCookieValue() {
        Cookie[] cookies = { new Cookie("cart", "") };

        String result = cookieService.parseCookieDecoder(cookies);

        assertEquals("", result);
    }@Test
    void extractCookie_ShouldReturnNull_WhenRequestHasNoCookies() {
        when(request.getCookies()).thenReturn(null);

        String result = cookieService.extractCookie(request, "testName");

        assertNull(result);
    }

    @Test
    void extractCookie_ShouldReturnValue_WhenCookieExists() {
        Cookie cookie = new Cookie("target", "value123");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String result = cookieService.extractCookie(request, "target");

        assertEquals("value123", result);
    }

    @Test
    void extractCookie_ShouldReturnNull_WhenCookieNotFound() {
        Cookie cookie = new Cookie("other", "value");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String result = cookieService.extractCookie(request, "target");

        assertNull(result);
    }
    @Test
    void getCartFromCookie_shouldReturnCart_whenCookieExists() {
        String cartJson = "{\"items\":[]}";
        String encodedJson = URLEncoder.encode(cartJson, StandardCharsets.UTF_8);
        Cookie cartCookie = new Cookie("cart", encodedJson);
        when(request.getCookies()).thenReturn(new Cookie[]{cartCookie});
        Cart cart = cookieService.getCartFromCookie(request);
        assertNotNull(cart);
    }
    @Test
    void getCartFromCookie_shouldReturnEmptyCart_whenNoCookie() {
        when(request.getCookies()).thenReturn(null);
        Cart cart = cookieService.getCartFromCookie(request);
        assertNotNull(cart);
    }
}