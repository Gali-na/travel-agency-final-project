package com.epam.travel_agency_final_project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    @NotBlank(message = "{error.email.required}")
    private String email;
    @NotBlank(message = "{error.password.required}")
    private String password;
}
