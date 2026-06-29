package com.epam.travel_agency_final_project.aspect.service.loggin.token;

import com.epam.travel_agency_final_project.service.RefreshTokenService;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
@Aspect
@Component
public class RotateTokenLogging {
    private static final Logger logger = LogManager.getLogger(RotateTokenLogging.class);
    @Pointcut("execution(* com.epam.travel_agency_final_project.service.RefreshTokenService.rotateRefreshToken(..))")
    public void rotateTokenMethods() {

    }
    @Before("rotateTokenMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] != null) {
            logger.debug("Starting rotation for token: {}", args[0]);
        } else {
            logger.warn("Rotation method called with null or missing arguments.");
        }
    }
    @AfterReturning(pointcut = "rotateTokenMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (result instanceof String) {
            logger.info("Refresh token rotated successfully. New token generated.");
        }
        if (result == null) {
            logger.warn("Token rotation failed: result returned null (invalid, expired, or blocked).");
        } else {
            logger.info("Rotation returned non-string result: {}", result);
        }
    }
    @AfterThrowing(pointcut = "rotateTokenMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception: {} - message: {}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "no message provided"));
    }
}
