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
        // 1. Пароль: 6-10 символів, цифри+букви, мін 1 велика, мін 1 мала
        String passRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]{6,10}$";
        if (!password.matches(passRegex)) {
            throw new ValidationException("error.password.invalid");
        }

        // 2. Email: тільки цифри, букви та @ (приклад: user1@mail)
        // Примітка: зазвичай email потребує крапки, але ви просили "тільки цифри, букви і @"
        if (!email.matches("^[a-zA-Z0-9]+@[a-zA-Z0-9]+$")) {
            throw new ValidationException("error.email.invalid");
        }

        // 3. Ім'я та Прізвище: тільки букви, до 10 символів
        String nameRegex = "^[a-zA-Zа-яА-ЯіІєЄїЇґҐ]{1,10}$";
        if (!firstName.matches(nameRegex)) {
            throw new ValidationException("error.name.invalid");
        }
        if (!lastName.matches(nameRegex)) {
            throw new ValidationException("error.lastname.invalid");
        }
    }
//
//    @NotBlank(message = "Email is required")
//    @Email(message = "Invalid email format")
//    private String email;
//
//    @NotBlank(message = "Password is required")
//    @Size(min = 6, message = "Password must be at least 6 characters long")
//    private String password;
//
//    @NotBlank(message = "First name is required")
//    private String firstName;
//
//    @NotBlank(message = "Last name is required")
//    private String lastName;
}
