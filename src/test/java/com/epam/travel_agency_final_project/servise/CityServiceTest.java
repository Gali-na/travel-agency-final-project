package com.epam.travel_agency_final_project.servise;
import com.epam.travel_agency_final_project.dto.CityDTO;
import com.epam.travel_agency_final_project.entity.City;
import com.epam.travel_agency_final_project.mapper.CityMapper;
import com.epam.travel_agency_final_project.repository.CityRepository;
import com.epam.travel_agency_final_project.service.CityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    private CityMapper cityMapper;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityService cityService;

    @Test
    @DisplayName("Should return list of CityDTO when cities exist")
    void findAll_ShouldReturnListOfCityDTO_WhenCitiesExist() {
        City city = new City();
        CityDTO cityDTO = new CityDTO();
        when(cityRepository.findAll()).thenReturn(List.of(city));
        when(cityMapper.toDTO(city)).thenReturn(cityDTO);
        List<CityDTO> result = cityService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(cityDTO);
        verify(cityRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no cities found")
    void findAll_ShouldReturnEmptyList_WhenNoCitiesFound() {
        when(cityRepository.findAll()).thenReturn(Collections.emptyList());
        List<CityDTO> result = cityService.findAll();
        assertThat(result).isEmpty();
        verify(cityMapper, never()).toDTO(any());
    }
}