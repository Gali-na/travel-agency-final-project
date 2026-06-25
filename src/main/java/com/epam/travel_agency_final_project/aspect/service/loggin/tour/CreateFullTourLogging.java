package com.epam.travel_agency_final_project.aspect.service.loggin.tour;

import com.epam.travel_agency_final_project.exeption.CityNotFoundException;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

@Aspect
@Component
public class CreateFullTourLogging {
    private static final Logger logger = LogManager.getLogger(CreateFullTourLogging.class);
    @Pointcut("execution(com.epam.travel_agency_final_project.service.TourService.createFullTour(..))")
    public void tourCreationMethods() {
    }
    @Before("tourCreationMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] != null) {
            logger.debug("Starting tour creation process for DTO: {}", args[0]);
        } else {
            logger.warn("createFullTour called with null or missing DTO.");
        }
    }
    @AfterThrowing(pointcut ="tourCreationMethods()",throwing = "exception")
    public void logAfterThrowingException(JoinPoint joinPoint, Exception exception) {
        Object[] args = joinPoint.getArgs();
        String dtoInfo = (args.length > 0) ? args[0].toString() : "unknown DTO";
        logger.error("Error occurred in createFullTour! DTO: {}. Exception: {}. Message: {}",
                dtoInfo, exception.getClass().getSimpleName(), exception.getMessage());
    }
}

