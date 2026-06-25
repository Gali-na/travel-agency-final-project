package com.epam.travel_agency_final_project.aspect.service.loggin.authentication;

import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserAuthenticationServiceLogging {
    private static final Logger logger = LogManager.getLogger(UserAuthenticationServiceLogging.class);
    @Pointcut("execution(* com.epam.travel_agency_final_project.service.UserAuthenticationService.getAuthenticatedUser(..))")
    public void authServiceMethods() {
    }
    @AfterReturning(pointcut = "authServiceMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (result == null) {
            logger.warn("getAuthenticatedUser returned null.");
            return;
        }
        if (result instanceof UserSecurityDTO user) {
            logger.info("User successfully authenticated. User ID: {}", user.getId());
        } else {
            logger.warn("getAuthenticatedUser returned unexpected result type: {}",
                    result.getClass().getName());
        }
    }
    @AfterThrowing(pointcut = "authServiceMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Authentication failed: {} - {}",
                ex.getClass().getSimpleName(),
                (ex.getMessage() != null ? ex.getMessage() : "No message"));
    }
}

