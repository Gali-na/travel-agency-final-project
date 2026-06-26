package com.epam.travel_agency_final_project.controller;
import com.epam.travel_agency_final_project.controller.InfoController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InfoControllerTest {

    private InfoController controller;

    @BeforeEach
    void setUp() {
        controller = new InfoController();
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
    void showInvalidEmailPage_ReturnsCheckoutInfoView() {
        assertEquals("checkoutInfo", controller.showInvalidEmailPage());
    }

    @Test
    void showTourCreatedPage_ReturnsTourCreatedView() {
        assertEquals("admin/tour-createdInfo", controller.showTourCreatedPage());
    }

    @Test
    void checkoutSuccess_ReturnsCheckoutSuccessView() {
        assertEquals("checkout-success", controller.checkoutSuccess());
    }
}