package com.epam.travel_agency_final_project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourFilter {
    private Boolean isHot;
    private String tourType;
    private String hotelType;
}
