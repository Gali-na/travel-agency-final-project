package com.epam.travel_agency_final_project.aspect.service.loggin.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LockUserLogging {
    private static final Logger logger = LogManager.getLogger(LockUserLogging.class);
    @Pointcut("execution(* com.epam.travel_agency_final_project.service.*.lockUser(java.util.UUID))")
    public void lockUserMethod() {
    }
    @Before("lockUserMethod()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] != null) {
            logger.info("Attempting to lock user with ID: {}", args[0]);
        } else {
            logger.warn("Attempting to lock user, but ID argument is missing or null.");
        }
    }
    @AfterReturning("lockUserMethod()")
    public void logAfterReturning() {
        logger.info("User locked successfully.");
    }

    @AfterThrowing(pointcut = "lockUserMethod()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception: {} - message: {}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "no message provided"));
    }
}
