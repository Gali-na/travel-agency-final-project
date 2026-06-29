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
public class DeleteRefreshTokenLogging {
    private static final Logger logger = LogManager.getLogger(DeleteRefreshTokenLogging.class);
    @Pointcut("execution(* com.epam.travel_agency_final_project.service.RefreshTokenService.deleteRefreshToken(..))")
    public void deleteTokenMethods() {
    }
    @Before("deleteTokenMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] != null) {
            logger.debug("Attempting to delete refresh token: {}", args[0]);
        } else {
            logger.warn("Attempt to delete refresh token called with null or missing argument.");
        }
    }
    @AfterReturning(pointcut = "deleteTokenMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (result instanceof Boolean) {
            boolean isDeleted = (Boolean) result;
            if (isDeleted) {
                logger.debug("Refresh token successfully deleted.");
            } else {
                logger.warn("Attempt to delete non-existent refresh token (returned false).");
            }
        } else {
            logger.warn("Unexpected return type from deleteRefreshToken: {}", (result == null ? "null" : result.getClass().getName()));
        }
    }
    @AfterThrowing(pointcut = "deleteTokenMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception: {} - message: {}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "no message provided"));
    }
}
