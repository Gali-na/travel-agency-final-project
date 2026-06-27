package com.epam.travel_agency_final_project.dto;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TourCreationDTO implements Validatable{

    @NotNull(message = "{error.price.required}")
    @DecimalMin(value = "1.00", message = "{error.price.min}")
    private BigDecimal price;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "{error.arrival.required}")
    @FutureOrPresent(message = "{error.arrival.future}")
    private LocalDateTime arrivalDate;
    @NotNull(message = "{error.eviction.required}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime evictionDate;

    @NotNull(message = "{error.hot.required}")
    private Boolean isHot;

    @NotBlank(message = "{error.image.required}")
    private String imagePath;

    @NotBlank(message = "{error.type.required}")
    private String tourType;

    @NotBlank(message = "{error.transfer.required}")
    private String transferType;
    @NotBlank(message = "{error.hotel.required}")
    private String hotelType;
    @NotNull(message = "{error.city.required}")
    private UUID cityId;
    @NotBlank(message = "{error.titleEn.required}")
    private String titleEn;
    @NotBlank(message = "{error.descEn.required}")
    private String descriptionEn;
    @NotBlank(message = "{error.titleUa.required}")
    private String titleUa;
    @NotBlank(message = "{error.descUa.required}")
    private String descriptionUa;

    @Override
    public void validate() {
        if (arrivalDate != null && evictionDate != null) {
            if (!evictionDate.isAfter(arrivalDate)) {
                throw new ValidationException("error.eviction.before.arrival");
            }
        }
    }
}
