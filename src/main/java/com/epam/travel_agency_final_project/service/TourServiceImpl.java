package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.dto.TourCreationDTO;
import com.epam.travel_agency_final_project.dto.TourDTO;
import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.dto.TourTranslationDTO;
import com.epam.travel_agency_final_project.entity.City;
import com.epam.travel_agency_final_project.entity.TourTranslation;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TourServiceImpl implements TourService {
    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final TourTranslationRepository translationRepository;
    private final TourCreateMapper tourCreateMapper;
    private final CityRepository cityRepository;

    @Transactional
    public void createFullTour(TourCreationDTO dto) {
        // 1. Знаходимо місто (це важливо для FOREIGN KEY)
        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));

        // 2. Мапимо і зберігаємо основний об'єкт
        TourDTO tourDto = tourCreateMapper.toTourDTO(dto);
        Tour tourEntity = tourCreateMapper.toEntity(tourDto, city);
        Tour savedTour = tourRepository.save(tourEntity);

        // 3. Створюємо переклади, передаючи вже збережений об'єкт savedTour
        tourCreateMapper.toUkTranslation(dto);


        TourTranslation uk = tourCreateMapper.toTranslationEntity(tourCreateMapper.toUkTranslation(dto), savedTour);
        TourTranslation en = tourCreateMapper.toTranslationEntity(tourCreateMapper.toEnTranslation(dto), savedTour);

        translationRepository.save(uk);
        translationRepository.save(en);
    }
    @Override
    public Page<TourFullDTO> getTours(
            String lang,
            TourFilter filter,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return tourRepository.findToursWithJdbc(lang, filter, pageable);
    }

    @Override
    public List<TourFullDTO> getToursForCart(Set<UUID> ids, String lang) {
        return tourRepository.findToursByIdsAndInLanguage(ids, lang);
    }

    @Override
    public Page<TourFullDTO> getAllToursByLanguage(
            String lang,
            int page,
            int size
    ) {
        // Замість дублювання SQL-запитів просто створюємо порожній фільтр
        TourFilter emptyFilter = new TourFilter();

        // Перевикористовуємо наш гнучкий JDBC метод
        return getTours(lang, emptyFilter, page, size);
    }
    @Override
    public TourFullDTO findById(UUID id, String lang){
       Optional<Tour> tour = tourRepository.findById(id);
        if(tour.isPresent()){
        return tourMapper.toDto(tour.get(),lang);
        }
       throw  new TourNotFoundException("Tour with ID " + id + " not found");
    }
}
/*
задача створити чторінку на якій ми зможемо створити нові тури
для цього зрендери сторінку на якій будуть поля для заповнення


    BigDecimal price;
     LocalDateTime arrivalDate;
     LocalDateTime evictionDate;
     boolean isHot;  має бути випадаючий список з FALSE aбо TRUE
     String imagePath;

     поля tourType, transferType, hotelType та їх списки з випадаючими значеннями мають відображатися залежно від мови локалізації
     але в при запиті мають бути відправлені тільки константи англійською мавою і верхньому реністрі

     private String tourType;  має бути випадаючий список для вибору   HEALTH, SPORTS, LEISURE, SAFARI, WINE, ECO, ADVENTURE, CULTURAL
     private String transferType;  має бути випадаючий список для вибору    BUS, TRAIN, PLANE, SHIP
     private String hotelType;     FOUR_STARS, FIVE_STARS


    private String cityName;  выдображаэмо випадаючий список залежно выд локалі, вибрати можна тільки один варіант, привиборі в запит має надсилатися ідентифікатор

    ці поля мають були в двох екземплярах
     англійский переклад
     title;
     description;


     український переклад   переклад
     title;
     description;


при надсианні форми ми маємо перевірити щоб всі поля були заповнені, жодного пустого поля

потрібно перевірити на контроллері щоб  LocalDateTime arrivalDate;
     LocalDateTime evictionDate; відповідали arrivalDate <evictionDate




*

*
* */