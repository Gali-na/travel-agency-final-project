package com.epam.travel_agency_final_project.aspect.service.loggin.tour;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class GetToursLogging {
    private static final Logger logger = LogManager.getLogger(GetToursLogging.class);
    @Pointcut("execution(com.epam.travel_agency_final_project.service.TourService.getTours(..))")
    public void getToursMethods() {

    }
    @Before("getToursMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length >= 4) {
            logger.debug("Fetching tours. Lang: {}, Page: {}, Size: {}", args[0], args[2], args[3]);
        }
    }

    @AfterReturning(pointcut = "getToursMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (result instanceof org.springframework.data.domain.Page<?>) {
            org.springframework.data.domain.Page<?> page = (org.springframework.data.domain.Page<?>) result;
            logger.debug("Tours fetched. Total elements: {}, Total pages: {}",
                    page.getTotalElements(), page.getTotalPages());
        }
    }
    @AfterThrowing(pointcut = "getToursMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception: {} - message: {}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "no message provided"));
    }
}
