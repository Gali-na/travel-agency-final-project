package com.epam.travel_agency_final_project.aspect.service.loggin.token;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
//
@Aspect
@Component
public class GetTokenLogging {
    private static final Logger logger = LogManager.getLogger(GetTokenLogging.class);
    @Pointcut("execution(com.epam.travel_agency_final_project.service.RefreshTokenService.getRefreshToken(..))")
    public void getTokenMethods() {}
    @Before("getTokenMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] != null) {
            logger.debug("Fetching refresh token: {}", args[0]);
        } else {
            logger.warn("getRefreshToken called with missing token argument.");
        }
    }

    @AfterReturning(pointcut = "getTokenMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (result != null) {
            logger.debug("Successfully retrieved token DTO. "+result);
        } else {
            logger.warn("Token not found (returned null).");
        }
    }
    @AfterThrowing(pointcut = "getTokenMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception: {} - message: {}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "no message provided"));
    }
}