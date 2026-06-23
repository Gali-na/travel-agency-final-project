package com.epam.travel_agency_final_project.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CityDTO {
    private UUID id;
    private String name; // Сюди ми мапитимемо перекладену назву
}