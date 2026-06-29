package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.dto.*;
import com.epam.travel_agency_final_project.entity.City;
import com.epam.travel_agency_final_project.entity.Tour;
import com.epam.travel_agency_final_project.entity.TourTranslation;
import com.epam.travel_agency_final_project.exeption.CityNotFoundException;
import com.epam.travel_agency_final_project.exeption.TourNotFoundException;
import com.epam.travel_agency_final_project.mapper.TourCreateMapper;
import com.epam.travel_agency_final_project.mapper.TourMapper;
import com.epam.travel_agency_final_project.model.TourFilter;
import com.epam.travel_agency_final_project.repository.CityRepository;
import com.epam.travel_agency_final_project.repository.TourRepository;
import com.epam.travel_agency_final_project.repository.TourTranslationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock
    private TourRepository tourRepository;
    @Mock
    private TourMapper tourMapper;
    @Mock
    private TourTranslationRepository translationRepository;
    @Mock
    private TourCreateMapper tourCreateMapper;
    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private TourService tourService;

    @Test
    void createFullTour_ShouldSaveTourAndTranslations_WhenCityExists() {
        UUID cityId = UUID.randomUUID();
        TourCreationDTO dto = new TourCreationDTO();
        dto.setCityId(cityId);

        City city = new City();
        TourDTO tourDto = new TourDTO();
        Tour tourEntity = new Tour();
        Tour savedTour = new Tour();

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        when(tourCreateMapper.toTourDTO(dto)).thenReturn(tourDto);
        when(tourCreateMapper.toEntity(tourDto, city)).thenReturn(tourEntity);
        when(tourRepository.save(tourEntity)).thenReturn(savedTour);
        TourTranslationDTO translationDto = new TourTranslationDTO();
        when(tourCreateMapper.toUkTranslation(dto)).thenReturn(translationDto);
        when(tourCreateMapper.toEnTranslation(dto)).thenReturn(translationDto);
        when(tourCreateMapper.toTranslationEntity(any(), eq(savedTour))).thenReturn(new TourTranslation());
        tourService.createFullTour(dto);

        verify(tourRepository, times(1)).save(tourEntity);
        verify(translationRepository, times(2)).save(any(TourTranslation.class));
    }

    @Test
    void createFullTour_ShouldThrowException_WhenCityNotFound() {
        UUID cityId = UUID.randomUUID();
        TourCreationDTO dto = new TourCreationDTO();
        dto.setCityId(cityId);
        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());
        assertThrows(CityNotFoundException.class, () -> tourService.createFullTour(dto));
        verify(tourRepository, never()).save(any());
        verify(translationRepository, never()).save(any());
    }

    @Test
    void getTours_ShouldReturnPageOfTours() {
        String lang = "uk";
        TourFilter filter = new TourFilter();
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<TourFullDTO> expectedPage = new PageImpl<>(Collections.emptyList());
        when(tourRepository.findToursWithJdbc(lang, filter, pageable)).thenReturn(expectedPage);
        Page<TourFullDTO> result = tourService.getTours(lang, filter, page, size);
        assertEquals(expectedPage, result);
        verify(tourRepository).findToursWithJdbc(lang, filter, pageable);
    }
    @Test
    void getToursForCart_ShouldReturnListOfTours() {
        Set<UUID> ids = Set.of(UUID.randomUUID(), UUID.randomUUID());
        String lang = "uk";
        List<TourFullDTO> expectedTours = Collections.emptyList();
        when(tourRepository.findToursByIdsAndInLanguage(ids, lang)).thenReturn(expectedTours);
        List<TourFullDTO> result = tourService.getToursForCart(ids, lang);
        assertEquals(expectedTours, result);
        verify(tourRepository, times(1)).findToursByIdsAndInLanguage(ids, lang);
    }

    @Test
    void getAllToursByLanguage_ShouldReturnPageOfTours() {
        String lang = "uk";
        int page = 0;
        int size = 10;
        Page<TourFullDTO> expectedPage = new PageImpl<>(Collections.emptyList());
        when(tourRepository.findToursWithJdbc(eq(lang), any(TourFilter.class), any())).thenReturn(expectedPage);
        Page<TourFullDTO> result = tourService.getAllToursByLanguage(lang, page, size);
        assertEquals(expectedPage, result);
        verify(tourRepository).findToursWithJdbc(eq(lang), any(TourFilter.class), any());
    }
    @Test
    void findById_ShouldReturnDto_WhenTourExists() {
        UUID id = UUID.randomUUID();
        String lang = "uk";
        Tour tour = new Tour();
        TourFullDTO expectedDto = new TourFullDTO();
        when(tourRepository.findById(id)).thenReturn(Optional.of(tour));
        when(tourMapper.toDto(tour, lang)).thenReturn(expectedDto);
        TourFullDTO result = tourService.findById(id, lang);
        assertEquals(expectedDto, result);
        verify(tourRepository).findById(id);
        verify(tourMapper).toDto(tour, lang);
    }

    @Test
    void findById_ShouldThrowException_WhenTourNotFound() {
        UUID id = UUID.randomUUID();
        String lang = "uk";
        when(tourRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(TourNotFoundException.class, () -> tourService.findById(id, lang));
        verify(tourRepository).findById(id);
        verify(tourMapper, never()).toDto(any(), anyString());
    }
    @Test
    void checkPaymentAvailability_ShouldReturnPositive_WhenBalanceIsGreater() {
        UserSecurityDTO user = UserSecurityDTO.builder()
                .balance(new BigDecimal("100.00"))
                .build();
        TourFullDTO tour = new TourFullDTO();
        tour.setPrice(new BigDecimal("50.00"));

        int result = tourService.checkPaymentAvailability(user, tour);

        assertTrue(result > 0);
    }

    @Test
    void checkPaymentAvailability_ShouldReturnZero_WhenBalanceEqualsPrice() {
        UserSecurityDTO user = UserSecurityDTO.builder()
                .balance(new BigDecimal("50.00"))
                .build();
        TourFullDTO tour = new TourFullDTO();
        tour.setPrice(new BigDecimal("50.00"));

        int result = tourService.checkPaymentAvailability(user, tour);

        assertEquals(0, result);
    }

    @Test
    void checkPaymentAvailability_ShouldReturnNegative_WhenBalanceIsLower() {
        UserSecurityDTO user = UserSecurityDTO.builder()
                .balance(new BigDecimal("20.00"))
                .build();
        TourFullDTO tour = new TourFullDTO();
        tour.setPrice(new BigDecimal("50.00"));

        int result = tourService.checkPaymentAvailability(user, tour);

        assertTrue(result < 0);
    }

    @Test
    void checkPaymentAvailability_ShouldHandleNullBalanceAsZero() {
        UserSecurityDTO user = UserSecurityDTO.builder()
                .balance(null)
                .build();
        TourFullDTO tour = new TourFullDTO();
        tour.setPrice(new BigDecimal("50.00"));

        int result = tourService.checkPaymentAvailability(user, tour);

        assertTrue(result < 0);
    }

    @Test
    void checkPaymentAvailability_ShouldHandleNullPriceAsZero() {
        UserSecurityDTO user = UserSecurityDTO.builder()
                .balance(new BigDecimal("50.00"))
                .build();
        TourFullDTO tour = new TourFullDTO();
        tour.setPrice(null);

        int result = tourService.checkPaymentAvailability(user, tour);

        assertTrue(result > 0);
    }
}