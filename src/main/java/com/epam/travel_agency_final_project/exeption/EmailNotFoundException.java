package com.epam.travel_agency_final_project.exeption;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String message) {

        super(message);
    }
}
