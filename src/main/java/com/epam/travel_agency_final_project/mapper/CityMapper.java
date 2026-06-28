package com.epam.travel_agency_final_project.mapper;

import com.epam.travel_agency_final_project.dto.CityDTO;
import com.epam.travel_agency_final_project.entity.City;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CityMapper {
    private final ModelMapper modelMapper;
    public CityMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.addConverter(new AbstractConverter<City, CityDTO>() {
            @Override
            protected CityDTO convert(City source) {
                CityDTO dto = new CityDTO();
                dto.setId(source.getId());
                String currentLang = LocaleContextHolder.getLocale().getLanguage();

                String name = source.getTranslations().stream()
                        .filter(t -> t.getId().getLang().equals(currentLang))
                        .map(translation -> translation.getName())
                        .findFirst()
                        .orElse(source.getTranslations().get(0).getName());
                dto.setName(name);
                return dto;
            }
        });
    }

    public CityDTO toDTO(City city) {
        return modelMapper.map(city, CityDTO.class);
    }
}