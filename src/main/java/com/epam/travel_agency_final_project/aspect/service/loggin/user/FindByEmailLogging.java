package com.epam.travel_agency_final_project.aspect.service.loggin.user;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class FindByEmailLogging {
    private static final Logger logger = LogManager.getLogger(FindByEmailLogging.class);
    @Pointcut("execution(com.epam.travel_agency_final_project.service.*.findByEmail(String))")
    public void findByEmailMethods() {

    }
    @Before("findByEmailMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof String) {
            logger.debug("Fetching security details for email: {}", args[0]);
        }
    }
    @AfterReturning(pointcut = "findByEmailMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (result != null) {
            logger.debug("Security details successfully retrieved for user.");
        } else {
            logger.warn("Security details not found for the provided email.");
        }
    }
    @AfterThrowing(pointcut = "findByEmailMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Error of type {} occurred : message{}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "No message provided"));
    }
}
