package com.epam.travel_agency_final_project.dto;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserRegistrationDTO implements Validatable {
    @NotBlank(message = "{error.email.required}")
    private String email;
    @NotBlank(message = "{error.password.required}")
    private String password;
    @NotBlank(message = "{error.firstname.required}")
    private String firstName;
    @NotBlank(message = "{error.lastname.required}")
    private String lastName;
    @Override
    public void validate() {
        String passRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]{6,10}$";
        if (!password.matches(passRegex)) {
            throw new ValidationException("error.password.invalid");
        }

        if (!email.matches("^[a-zA-Z0-9]+@[a-zA-Z0-9]+$")) {
            throw new ValidationException("error.email.invalid");
        }

        String nameRegex = "^[a-zA-Zа-яА-ЯіІєЄїЇґҐ]{1,10}$";
        if (!firstName.matches(nameRegex)) {
            throw new ValidationException("error.name.invalid");
        }
        if (!lastName.matches(nameRegex)) {
            throw new ValidationException("error.lastname.invalid");
        }
    }
}
