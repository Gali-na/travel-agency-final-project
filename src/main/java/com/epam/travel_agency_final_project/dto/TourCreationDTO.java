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
    private Boolean isHot;/**/
    @NotBlank
    private String imagePath;  //не додане поле на форму
    @NotBlank(message = "Tour type cannot be empty")
    private String tourType;
    @NotBlank(message = "Transfer type cannot be empty")
    private String transferType;
    @NotBlank(message = "Hotel type cannot be empty")
    private String hotelType;
    @NotNull(message = "City is required")
    private UUID cityId; // Приймаємо ID міста  /**/

    @NotBlank(message = "TitleEn cannot be empty")
    @NotBlank private String titleEn;
    @NotBlank(message = "DescriptionEn cannot be empty")
    @NotBlank private String descriptionEn;

    @NotBlank(message = "TitleUa cannot be empty")
    @NotBlank private String titleUa;
    @NotBlank(message = "DescriptionUK cannot be empty")
    private String descriptionUa;




//    @NotBlank private String imagePath;+
//    @NotBlank private String tourType;
//    @NotBlank private String transferType;
//    @NotBlank private String hotelType;
//    @NotNull private Long cityId;
//    @NotBlank private String titleEn;
//    @NotBlank private String descriptionEn;
//    @NotBlank private String titleUa;
//    @NotBlank private String descriptionUa;






/*CREATE TABLE tours (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,  -
    price DECIMAL(10, 2) NOT NULL,   +
    city_id UUID NOT NULL,             +
    arrival_date TIMESTAMP NOT NULL,   +
    eviction_date TIMESTAMP NOT NULL,  +
    is_hot BOOLEAN DEFAULT FALSE,     +
    FOREIGN KEY (city_id) REFERENCES cities(id)+
);

CREATE TABLE tours_translations (
    tours_id UUID NOT NULL,  -
    lang VARCHAR(5) NOT NULL,  -
    title VARCHAR(255) NOT NULL,  -
    description TEXT NOT NULL,  -
    tour_type VARCHAR(50) NOT NULL,  +
    transfer_type VARCHAR(50) NOT NULL,  +
    hotel_type VARCHAR(50) NOT NULL,   +
    PRIMARY KEY (tours_id, lang),
    FOREIGN KEY (tours_id) REFERENCES tours(id) ON DELETE CASCADE
);*/


    /* пропоную створити  TourDTO
    * в якому будуть поля для збереження Tour в базу
    *  price DECIMAL(10, 2) NOT NULL,   +
    city_id UUID NOT NULL,             +
    arrival_date TIMESTAMP NOT NULL,   +
    eviction_date TIMESTAMP NOT NULL,  +
    is_hot BOOLEAN DEFAULT FALSE,     +
    FOREIGN KEY (city_id) REFERENCES cities(id)
    *
    * ми їх збережемо в  в базу, отримаємо  ідентифікатор  туру і робимо запис з цим ідентифікатором в таблицю
    * tours_translations
    *
    * дя цього зробимо TourTranslationsDTO   в якому будуть поля
    *
    * CREATE TABLE tours_translations (
    tours_id UUID NOT NULL,
    lang VARCHAR(5) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    tour_type VARCHAR(50) NOT NULL,
    transfer_type VARCHAR(50) NOT NULL,
    hotel_type VARCHAR(50) NOT NULL,
    *
    * в контроллері створимо 1 TourDTO  і TourTranslationsDTO в залежності від мови і дістанемо з
    *
    * TourCreationDTO всі поля, проте перед цим TourCreationDTO  треба доповнити
    *
    *
    * */



//    @NotNull @DecimalMin("0.01") private BigDecimal price;
//    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) private LocalDateTime arrivalDate;
//    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) private LocalDateTime evictionDate;
//
//    private boolean isHot;
//    @NotBlank private String imagePath;+
//    @NotBlank private String tourType;
//    @NotBlank private String transferType;
//    @NotBlank private String hotelType;
//    @NotNull private Long cityId;
//    @NotBlank private String titleEn;
//    @NotBlank private String descriptionEn;
//    @NotBlank private String titleUa;
//    @NotBlank private String descriptionUa;
}
