package com.epam.travel_agency_final_project.aspect.service.loggin.user;

import com.epam.travel_agency_final_project.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class FindByEmailExactLogging {
    private static final Logger logger = LogManager.getLogger(FindByEmailExactLogging.class);

    @Pointcut("execution(* com.epam.travel_agency_final_project.service.*.findByEmailExact(String, org.springframework.data.domain.Pageable))")
    public void findByEmailMethods() {
    }
    @Before("findByEmailMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length >= 2 && args[0] instanceof String) {
            logger.debug("Searching user by exact email: {}. Page: {}", args[0],
                    (args[1] instanceof Pageable ? ((Pageable) args[1]).getPageNumber() : "N/A"));
        } else {
            logger.warn("findByEmailExact called with invalid arguments.");
        }
    }
    @AfterReturning(pointcut = "findByEmailMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (result instanceof Page<?>) {
            Page<?> page = (Page<?>) result;
            logger.debug("Search by email returned {} results.", page.getTotalElements());
        }
    }
    @AfterThrowing(pointcut = "blockUserPointcut()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception: {} - message: {}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "no message provided"));
    }
}
