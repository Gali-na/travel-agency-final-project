package com.epam.travel_agency_final_project.aspect.service.loggin.tour;

import com.epam.travel_agency_final_project.exeption.TourNotFoundException;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import java.util.UUID;

@Aspect
@Component
public class FindByIdLogging {
    private static final Logger logger = LogManager.getLogger(FindByIdLogging.class);
    @Pointcut("execution(com.epam.travel_agency_final_project.service.TourService.findById(java.util.UUID, String))")
    public void findByIdMethods() {

    }
    @Before("findByIdMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length >= 2 && args[0] instanceof UUID) {
            logger.debug("Attempting to find tour by ID: {} with language: {}", args[0], args[1]);
        } else {
            logger.warn("findById called with invalid or missing arguments.");
        }
    }
    @AfterReturning(pointcut = "findByIdMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (result != null) {
            logger.debug("Successfully found tour.");
        } else {
            logger.warn("findById returned null unexpectedly.");
        }
    }
    @AfterThrowing(pointcut = "findByIdMethods()", throwing = "exception")
    public void logAfterThrowing(TourNotFoundException exception) {
        logger.error("Exception: {} - message: {}",
                exception.getClass().getName(),
                (exception.getMessage() != null ? exception.getMessage() : "no message provided"));
    }
}
