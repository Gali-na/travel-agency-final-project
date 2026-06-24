package com.epam.travel_agency_final_project.aspect.service.loggin.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
//
@Aspect
@Component
public class AuthLogging {
    private static final Logger logger = LogManager.getLogger(AuthLogging.class);
    @Pointcut("execution(com.epam.travel_agency_final_project.service.*.authenticate(String, String))")
    public void authMethods() {
    }
    @Before("authMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length >= 1 && args[0] instanceof String) {
            logger.info("Authentication attempt for email: {}", args[0]);
        } else {
            logger.info("Authentication attempt for email: null ");
        }
    }

    @AfterReturning(pointcut = "authMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (result instanceof Boolean) {
            boolean isAuthenticated = (Boolean) result;
            if (isAuthenticated) {
                logger.info("Authentication successful.");
            } else {
                logger.warn("Authentication failed: invalid credentials.");
            }
        }
    }
    @AfterThrowing(pointcut = "authMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception: {} - message: {}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "no message provided"));
    }
}
