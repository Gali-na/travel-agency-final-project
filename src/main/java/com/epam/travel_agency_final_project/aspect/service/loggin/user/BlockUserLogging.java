package com.epam.travel_agency_final_project.aspect.service.loggin.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
//
@Aspect
@Component
public class BlockUserLogging {
    private static final Logger logger = LogManager.getLogger(BlockUserLogging.class);
    @Pointcut("execution(* com.epam.travel_agency_final_project.service.UserService.blockUser(String))")
    public void blockUserPointcut() {}
    @Before("blockUserPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] != null) {
            logger.info("Attempting to block user with email: {}", args[0]);
        } else {
            logger.warn("Attempting to block user, but email argument is missing or null.");
        }
    }

    @AfterReturning("blockUserPointcut()")
    public void logAfterReturning() {
        logger.info("Block user operation completed.");
    }

    @AfterThrowing(pointcut = "blockUserPointcut()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception: {} - message: {}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "no message provided"));
    }
}
