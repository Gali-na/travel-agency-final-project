package com.epam.travel_agency_final_project.aspect.service.loggin.tour;
import com.epam.travel_agency_final_project.service.TourService;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

import java.util.List;
import java.util.Set;
@Aspect
@Component
public class GetToursForCartLogging {
    private static final Logger logger = LogManager.getLogger(GetToursForCartLogging.class);
    @Pointcut("execution(* com.epam.travel_agency_final_project.service.TourService.getToursForCart(..))")
    public void getToursForCartMethods() {}
    @Before("getToursForCartMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args != null && args.length >= 2 && args[0] instanceof Set<?>) {
            Set<?> id = (Set<?>) args[0];
            logger.debug("Fetching tours for cart. Requested IDs count: {}, Language: {}", id.size(), args[1]);
        } else {
            logger.warn("getToursForCart called with invalid arguments.");
        }
    }
    @AfterReturning(pointcut = "getToursForCartMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (result instanceof List<?>) {
            List<?> tours = (List<?>) result;
            logger.debug("Successfully retrieved {} tours for cart.", tours.size());
        } else if (result == null) {
            logger.warn("getToursForCart returned null.");
        }
    }

    @AfterThrowing(pointcut = "getToursForCartMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception: {} - message: {}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "no message provided"));
    }
}
