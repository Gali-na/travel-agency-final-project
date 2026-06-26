package com.epam.travel_agency_final_project.servise;

import com.epam.travel_agency_final_project.exeption.TourNotFoundException;
import com.epam.travel_agency_final_project.model.Cart;
import com.epam.travel_agency_final_project.service.CookieService;
import org.junit.jupiter.api.BeforeEach;
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
    @BeforeEach
    void setUp() {
        cookieService = new CookieService();
        response = mock(HttpServletResponse.class);
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
    }
    @Test
    void updateAuthCookies_ShouldAddCorrectCookie() {
        String token = "test-token-123";
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        cookieService.updateAuthCookies(response, token);

        verify(response).addCookie(cookieCaptor.capture());
        Cookie capturedCookie = cookieCaptor.getValue();

        assertEquals("access_token", capturedCookie.getName());
        assertEquals(token, capturedCookie.getValue());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals("/", capturedCookie.getPath());
        assertEquals(2592000, capturedCookie.getMaxAge());
    }

    @Test
    void updateAuthCookies_ShouldHandleNullToken() {
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        cookieService.updateAuthCookies(response, null);

        verify(response).addCookie(cookieCaptor.capture());
        assertNull(cookieCaptor.getValue().getValue());
    }
    @Test
    void extractCookieJWT_ShouldReturnToken_WhenCookieExists() {
        String cookieName = "jwt";
        String cookieValue = "test-token";
        Cookie[] cookies = {new Cookie(cookieName, cookieValue)};

        when(request.getCookies()).thenReturn(cookies);

        String result = cookieService.extractCookieJWT(request, cookieName);

        assertEquals(cookieValue, result);
    }

    @Test
    void extractCookieJWT_ShouldReturnNull_WhenCookiesAreNull() {
        when(request.getCookies()).thenReturn(null);

        String result = cookieService.extractCookieJWT(request, "jwt");

        assertNull(result);
    }

    @Test
    void extractCookieJWT_ShouldReturnNull_WhenCookieNotFound() {
        Cookie[] cookies = {new Cookie("other", "value")};
        when(request.getCookies()).thenReturn(cookies);

        String result = cookieService.extractCookieJWT(request, "jwt");

        assertNull(result);
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