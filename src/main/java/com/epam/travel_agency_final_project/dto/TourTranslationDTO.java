package com.epam.travel_agency_final_project.dto;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourTranslationDTO {
    private String lang;
    private String title;
    private String description;
    private String tourType;
    private String transferType;
    private String hotelType;
}
