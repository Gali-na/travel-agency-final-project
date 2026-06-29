package com.epam.travel_agency_final_project.exeption;

public class TourNotFoundException extends RuntimeException {
    public TourNotFoundException(String message) {

        super(message);
    }
}
