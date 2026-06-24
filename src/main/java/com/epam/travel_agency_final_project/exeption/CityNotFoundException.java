package com.epam.travel_agency_final_project.exeption;

public class CityNotFoundException  extends RuntimeException {
    public CityNotFoundException(String message) {
        super(message);
    }
}
