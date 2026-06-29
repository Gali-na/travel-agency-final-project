package com.epam.travel_agency_final_project.controller;
import com.epam.travel_agency_final_project.controller.InfoController;
import com.epam.travel_agency_final_project.service.UserAuthenticationService;
import com.epam.travel_agency_final_project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(MockitoExtension.class)
class InfoControllerTest {

    @Mock
    private UserService userService;
    @Mock private MessageSource messageSource;
    @Mock private UserAuthenticationService userAuthenticationService;
    @InjectMocks
    private InfoController infoController;
    private InfoController controller;
    @BeforeEach

    void setUp() {controller = new InfoController();

    }
    @Test
    void showBlockedPage_ReturnsBlockedView() {
        assertEquals("blocked", controller.showBlockedPage());
    }

    @Test
    void showTourNotFoundPage_ReturnsTourNotFoundView() {
        assertEquals("tour-not-found", controller.showTourNotFoundPage());
    }

    @Test
    void showCheckoutInfoPage_ReturnsCheckoutInfoView() {
        assertEquals("checkoutInfo", controller.showCheckoutInfoPage());
    }
    @Test
    void showTourCreatedPage_ReturnsTourCreatedView() {
        assertEquals("admin/tour-createdInfo", controller.showTourCreatedPage());
    }

    @Test
    void checkoutSuccess_ReturnsCheckoutSuccessView() {
        assertEquals("checkout-success", controller.checkoutSuccess());
    }
    @Test
    void showInvalidEmailPage_ReturnsAdminInvalidEmailView() {
        assertEquals("/admin/invalid-email", controller.showInvalidEmailPage());
    }
}