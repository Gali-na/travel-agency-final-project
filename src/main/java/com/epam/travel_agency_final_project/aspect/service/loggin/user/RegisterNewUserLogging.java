package com.epam.travel_agency_final_project.aspect.service.loggin.user;
import com.epam.travel_agency_final_project.dto.UserRegistrationDTO;
import com.epam.travel_agency_final_project.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Aspect
@Component
public class RegisterNewUserLogging {
    private static final Logger logger = LogManager.getLogger(RegisterNewUserLogging.class);
    @Pointcut("execution(* com.epam.travel_agency_final_project.service.*.registerNewUser(..))")
    public void registrationMethods() {}
    @Before("registrationMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof UserRegistrationDTO) {
            UserRegistrationDTO dto = (UserRegistrationDTO) args[0];
            logger.info("Starting registration for user: {}", dto.getEmail());
        }else {
            logger.info("Starting registration for user: {null}" );
        }
    }
    @AfterReturning(pointcut = "registrationMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        if (result instanceof UUID) {
            logger.info("User registered successfully. Assigned ID: {}", result);
        } else {
            logger.warn("registerNewUser returned unexpected result type.");
        }
    }
    @AfterThrowing(pointcut = "registrationMethods()", throwing = "ex")
        public void logAfterThrowing(Exception ex) {
        logger.error("Registration failed: {} - {}",
                ex.getClass().getSimpleName(),
                (ex.getMessage() != null ? ex.getMessage() : "No message"));

        }
}
