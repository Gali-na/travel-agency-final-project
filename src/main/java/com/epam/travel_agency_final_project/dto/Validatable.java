package com.epam.travel_agency_final_project.dto;

import jakarta.validation.ValidationException;

public interface Validatable {
    void validate() throws ValidationException;
}