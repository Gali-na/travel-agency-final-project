package com.epam.travel_agency_final_project.aspect.service.loggin.tour;

import com.epam.travel_agency_final_project.service.TourService;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

@Aspect
@Component
public class GetAllToursByLanguageLogging {
    private static final Logger logger = LogManager.getLogger(GetAllToursByLanguageLogging.class);

    @Pointcut("execution(* com.epam.travel_agency_final_project.service.TourService.getAllToursByLanguage(..))")
    public void getAllToursMethods() {
    }

    @Before("getAllToursMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length >= 3) {
            String lang = (args[0] instanceof String) ? (String) args[0] : "unknown";
            logger.debug("Requesting all tours by language: {}. Page: {}, Size: {}", lang, args[1], args[2]);
        }
        else {
            logger.debug("method argument invalid");
        }
    }

    @AfterReturning(pointcut = "getAllToursMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (result instanceof org.springframework.data.domain.Page<?>) {
            org.springframework.data.domain.Page<?> page = (org.springframework.data.domain.Page<?>) result;
            logger.debug("Successfully retrieved page of tours. Total items: {}", page.getTotalElements());
        } else if (result == null) {
            logger.warn("getAllToursByLanguage returned null.");
        }
    }
    @AfterThrowing(pointcut = "getAllToursMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception: {} - message: {}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "no message provided"));
    }
}
