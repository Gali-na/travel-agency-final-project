package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.dto.CityDTO;
import com.epam.travel_agency_final_project.mapper.CityMapper;
import com.epam.travel_agency_final_project.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CityService {
    private final CityMapper cityMapper;
    private final CityRepository cityRepository;
    public List<CityDTO> findAll() {
        return cityRepository.findAll().stream()
                .map(cityMapper::toDTO)
                .collect(Collectors.toList());
    }
}
