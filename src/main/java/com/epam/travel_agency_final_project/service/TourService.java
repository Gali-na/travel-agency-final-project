package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.dto.TourCreationDTO;
import com.epam.travel_agency_final_project.dto.TourDTO;
import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.entity.City;
import com.epam.travel_agency_final_project.entity.TourTranslation;
import com.epam.travel_agency_final_project.exeption.CityNotFoundException;
import com.epam.travel_agency_final_project.exeption.TourNotFoundException;
import com.epam.travel_agency_final_project.mapper.TourCreateMapper;
import com.epam.travel_agency_final_project.mapper.TourMapper;
import com.epam.travel_agency_final_project.entity.Tour;
import com.epam.travel_agency_final_project.model.TourFilter;
import com.epam.travel_agency_final_project.repository.CityRepository;
import com.epam.travel_agency_final_project.repository.TourRepository;
import com.epam.travel_agency_final_project.repository.TourTranslationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TourService{
    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final TourTranslationRepository translationRepository;
    private final TourCreateMapper tourCreateMapper;
    private final CityRepository cityRepository;

    @Transactional
    public void createFullTour(TourCreationDTO dto) {
        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new CityNotFoundException("City not found"));
        TourDTO tourDto = tourCreateMapper.toTourDTO(dto);
        Tour tourEntity = tourCreateMapper.toEntity(tourDto, city);
        Tour savedTour = tourRepository.save(tourEntity);
        tourCreateMapper.toUkTranslation(dto);
        TourTranslation uk = tourCreateMapper.toTranslationEntity(tourCreateMapper.toUkTranslation(dto), savedTour);
        TourTranslation en = tourCreateMapper.toTranslationEntity(tourCreateMapper.toEnTranslation(dto), savedTour);
        translationRepository.save(uk);
        translationRepository.save(en);
    }

    public Page<TourFullDTO> getTours(String lang, TourFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tourRepository.findToursWithJdbc(lang, filter, pageable);
    }
    public List<TourFullDTO> getToursForCart(Set<UUID> ids, String lang) {
        return tourRepository.findToursByIdsAndInLanguage(ids, lang);
    }
    public Page<TourFullDTO> getAllToursByLanguage(String lang, int page, int size) {
        TourFilter emptyFilter = new TourFilter();
        return getTours(lang, emptyFilter, page, size);
    }

    public TourFullDTO findById(UUID id, String lang){
       Optional<Tour> tour = tourRepository.findById(id);
        if(tour.isPresent()){
        return tourMapper.toDto(tour.get(),lang);
        }
       throw  new TourNotFoundException("Tour with ID " + id + " not found");
    }
    public int checkPaymentAvailability(UserSecurityDTO userSecurityDTO, TourFullDTO tourDTO){
        BigDecimal balance = (userSecurityDTO.getBalance() != null) ? userSecurityDTO.getBalance() : BigDecimal.ZERO;
        BigDecimal price = (tourDTO.getPrice() != null) ? tourDTO.getPrice() : BigDecimal.ZERO;
        return balance.compareTo(price);
    }
}
