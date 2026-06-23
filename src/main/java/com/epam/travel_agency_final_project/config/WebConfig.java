package com.epam.travel_agency_final_project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // Усі URL, що починаються з /uploads/,
//        // Spring перенаправить у зовнішню папку на диску
//        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:C:/travel-agency-uploads/tours/");
//    }

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Усі запити, що починаються з /uploads/, Spring шукатиме в нашій папці
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadDir);
    }

    @Bean
    public LocaleResolver localeResolver() {
        // Передаємо назву куки "lang" прямо в конструктор
        CookieLocaleResolver resolver = new CookieLocaleResolver("lang");
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        // Вказуємо, який саме параметр шукати в URL (у нашому випадку це lang)
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Реєструємо наш перехоплювач у системі Spring MVC
        registry.addInterceptor(localeChangeInterceptor());
    }

}