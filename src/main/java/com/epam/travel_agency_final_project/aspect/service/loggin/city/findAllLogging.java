package com.epam.travel_agency_final_project.aspect.service.loggin.city;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class findAllLogging {
    private static final Logger logger = LogManager.getLogger(findAllLogging.class);
    @Pointcut("execution(com.epam.travel_agency_final_project.service.CityService.findAll(..))")
    public void findAllMethod() {
    }
    @AfterReturning(pointcut = "findAllMethod()", returning = "result")
    public void logAfterFindAll(JoinPoint joinPoint, Object result) {
        Object[] args = (joinPoint != null) ? joinPoint.getArgs() : null;
        int argsCount = (args != null) ? args.length : 0;
        if (logger.isDebugEnabled()) {
            logger.debug("Executing findAll. Method argument count: {}", argsCount);
            if (result == null) {
                logger.debug("The result of the findAllMethod() method is null");
            } else if (result instanceof List<?>) {
                List<?> list = (List<?>) result;
                logger.debug("Number of cities found: {}", list.size());
            }
        }
    }
    @AfterThrowing(pointcut = "findAllMethod()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception: {} - message: {}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "no message provided"));
    }

}
