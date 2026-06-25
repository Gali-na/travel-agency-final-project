package com.epam.travel_agency_final_project.aspect.service.loggin.user;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Aspect
@Component
public class IsBlockUserLogging {
    private static final Logger logger = LogManager.getLogger(IsBlockUserLogging.class);

    @Pointcut("execution(* com.epam.travel_agency_final_project.service.*.isBlockUser(java.util.UUID))")
    public void blockStatusMethods() {
    }
    @Before("blockStatusMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof UUID) {
            logger.debug("Checking block status for user ID: {}", args[0]);
        }else {
            logger.debug("Invalid or missing argument");
        }
    }

    @AfterReturning(pointcut = "blockStatusMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (result instanceof Boolean) {
            boolean isLocked = (Boolean) result;
            logger.debug("User block status: {}", isLocked ? "LOCKED" : "ACTIVE");
        } else {
            logger.debug("isBlockUser returned unexpected type: {}",
                    (result == null ? "null" : result.getClass().getName()));
        }
    }
    @AfterThrowing(pointcut = "blockStatusMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception: {} - message: {}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "no message provided"));
    }
}
