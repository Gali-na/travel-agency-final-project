package com.epam.travel_agency_final_project.aspect.service.loggin.cooki;

import com.epam.travel_agency_final_project.exeption.TourNotFoundException;
import com.epam.travel_agency_final_project.service.CookieService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Aspect
@Component
public class UpdateCartCookieAfterPurchaseLogging {
    private static final Logger logger = LogManager.getLogger(UpdateCartCookieAfterPurchaseLogging.class);
    @Pointcut("execution(* com.epam.travel_agency_final_project.service.СookieServiсe.updateCartCookieAfterPurchase(..))")
    public void UpdateCartCookieAfterPurchase() {
    }
    @Before("cartCookieMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof UUID) {
            logger.info("Attempting to update cart cookie after purchase for tour ID: {}", args[0]);
        }
    }
    @AfterReturning(pointcut = "cartCookieMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (Boolean.TRUE.equals(result)) {
            logger.info("Cart cookie updated successfully after purchase.");
        }
    }
    @AfterThrowing(pointcut = "cartCookieMethods()", throwing = "ex")
    public void logAfterThrowing(TourNotFoundException ex) {
        if (ex != null) {
            logger.error("Failed to update cart cookie: {}",
                    (ex.getMessage() != null ? ex.getMessage() : "Unknown error"));
        }
    }
}
