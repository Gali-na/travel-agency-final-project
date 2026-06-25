package com.epam.travel_agency_final_project.dto;

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
public class TourCreationDTO {
    @NotNull(message = "Price is required")
    @DecimalMin(value = "1.00", message = "Price must be at least 1")
    private BigDecimal price;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "Arrival date is required")
    @FutureOrPresent(message = "Arrival date must be in the present or future")
    private LocalDateTime arrivalDate;
    @NotNull(message = "Eviction date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime evictionDate;/**/
    @NotNull(message = "IsHot status is required")
    private Boolean isHot;
    @NotBlank
    private String imagePath;
    @NotBlank(message = "Tour type cannot be empty")
    private String tourType;
    @NotBlank(message = "Transfer type cannot be empty")
    private String transferType;
    @NotBlank(message = "Hotel type cannot be empty")
    private String hotelType;
    @NotNull(message = "City is required")
    private UUID cityId;
    @NotBlank(message = "TitleEn cannot be empty")
    @NotBlank private String titleEn;
    @NotBlank(message = "DescriptionEn cannot be empty")
    @NotBlank private String descriptionEn;
    @NotBlank(message = "TitleUa cannot be empty")
    @NotBlank private String titleUa;
    @NotBlank(message = "DescriptionUK cannot be empty")
    private String descriptionUa;
}
