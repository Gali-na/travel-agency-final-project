package com.epam.travel_agency_final_project.controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.mock.web.MockHttpServletRequest;
import jakarta.servlet.RequestDispatcher;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class CustomErrorControllerManualTest {
    private CustomErrorController controller;
    @Mock
    private MessageSource messageSource;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new CustomErrorController(messageSource);
    }

    @Test
    void testHandleError_404() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404);
        ExtendedModelMap model = new ExtendedModelMap();

        when(messageSource.getMessage(eq("error.client.title"), any(), any(Locale.class)))
                .thenReturn("Client Error");
        when(messageSource.getMessage(eq("error.client.message"), any(), any(Locale.class)))
                .thenReturn("Not Found");
        String viewName = controller.handleError(request, model, Locale.ENGLISH);
        assertEquals("admin/error_page", viewName);
        assertEquals(404, model.get("statusCode"));
        assertEquals("Client Error", model.get("title"));
    }
}
