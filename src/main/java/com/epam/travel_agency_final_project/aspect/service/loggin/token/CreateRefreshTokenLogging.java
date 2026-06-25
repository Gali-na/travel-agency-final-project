package com.epam.travel_agency_final_project.aspect.service.loggin.token;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class CreateRefreshTokenLogging {
    private static final Logger logger = LogManager.getLogger(CreateRefreshTokenLogging.class);
    @Pointcut("execution(com.epam.travel_agency_final_project.service.RefreshTokenService.createRefreshToken(..))")
    public void refreshTokenMethods() {
    }
    @Before("refreshTokenMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        logger.debug("Executing createRefreshToken. Arguments: {}", Arrays.toString(args));
    }
    @AfterThrowing(pointcut = "refreshTokenMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception: {} - message: {}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "no message provided"));
    }
}
