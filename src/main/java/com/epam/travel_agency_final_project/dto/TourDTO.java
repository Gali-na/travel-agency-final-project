package com.epam.travel_agency_final_project.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourDTO {
    private BigDecimal price;
    private UUID cityId;
    private LocalDateTime arrivalDate;
    private LocalDateTime evictionDate;
    private Boolean isHot;
    private String imagePath;
}
