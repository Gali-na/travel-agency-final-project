package com.epam.travel_agency_final_project.aspect.service.loggin.user;
import com.epam.travel_agency_final_project.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;
@Aspect
@Component
public class IncreaseBalanceLogging {
    private static final Logger logger = LogManager.getLogger(IncreaseBalanceLogging.class);
    @Pointcut("execution( com.epam.travel_agency_final_project.service.*.increaseBalance(java.util.UUID, java.math.BigDecimal))")
    public void increaseBalancePointcut() {

    }
    @Before("increaseBalancePointcut()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length >= 2 && args[0] != null && args[1] != null) {
            logger.info("Increasing balance for user: {}. Amount: {}", args[0], args[1]);
        } else {
            logger.warn("Method increaseBalance called with missing or null arguments.");
        }
    }
    @AfterReturning(pointcut = "increaseBalancePointcut()", returning = "result")
    public void logAfterReturning(Object result) {
        logger.info("Balance updated successfully. New balance: {}", result);
    }
    @AfterThrowing(pointcut = "increaseBalancePointcut()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception: {} - message: {}",
                ex.getClass().getName(),
                (ex.getMessage() != null ? ex.getMessage() : "no message provided"));
    }
}
