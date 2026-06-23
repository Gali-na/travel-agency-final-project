package com.epam.travel_agency_final_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourFullDTO {
    private UUID id;
    private BigDecimal price;
    private UUID cityId;
    private LocalDateTime arrivalDate;
    private LocalDateTime evictionDate;
    private boolean isHot;
    private String imagePath;

    // Поля з таблиці перекладів туру
    private String title;
    private String description;
    private String tourType;
    private String transferType;
    private String hotelType;

    // НОВЕ ПОЛЕ: Назва міста з таблиці перекладів міст
    private String cityName;
    private Integer quantity;
}
