package com.epam.travel_agency_final_project.aspect.service.loggin.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Aspect
@Component
public class FindsUserByIdLogging {
    private static final Logger logger = LogManager.getLogger(FindsUserByIdLogging.class);
    @Pointcut("execution( com.epam.travel_agency_final_project.service.*.findById(java.util.UUID))")
    public void findByIdMethods() {
    }
    @Before("findByIdMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof UUID) {
            logger.debug("Fetching security details for user ID: {}", args[0]);
        }
        else {
            logger.debug("Invalid or missing argument");
        }
    }
    @AfterReturning(pointcut = "findByIdMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (result != null) {
            logger.debug("Security details successfully retrieved.");
        } else {
            logger.warn("Security details not found for the given ID.");
        }
    }
    @AfterThrowing(pointcut ="findByIdMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        if (ex != null) {
            logger.error("Error during security retrieval: {}",
                    (ex.getMessage() != null ? ex.getMessage() : "Unknown error"));
        }
    }
}
