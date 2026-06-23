package com.epam.travel_agency_final_project.config;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Додаткове налаштування (корисно для складних мапінгів, щоб уникнути конфліктів)
        mapper.getConfiguration()
                .setAmbiguityIgnored(true);

        return mapper;
    }
}
