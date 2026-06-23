package com.epam.travel_agency_final_project.exeption;

public class AuthenticationTokenMissingException extends RuntimeException {
    public AuthenticationTokenMissingException(String message) {
        super(message);
    }
}
