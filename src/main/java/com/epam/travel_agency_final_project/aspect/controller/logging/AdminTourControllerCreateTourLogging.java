package com.epam.travel_agency_final_project.aspect.controller.logging;
import com.epam.travel_agency_final_project.dto.TourCreationDTO;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.aspectj.lang.annotation.Before;
import org.springframework.validation.BindingResult;

@Aspect
@Component
public class AdminTourControllerCreateTourLogging {
    private static final Logger logger = LogManager.getLogger( AdminTourControllerCreateTourLogging.class);
    @Before("execution(* com.epam.travel_agency_final_project.controller.TourController.createTour(..))")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length >= 2 && args[0] instanceof TourCreationDTO) {
            TourCreationDTO dto = (TourCreationDTO) args[0];
            BindingResult bindingResult = (BindingResult) args[1];
            logger.info("Attempting to create tour. DTO state: {}", dto.toString());
            if (bindingResult != null && bindingResult.hasErrors()) {
                logger.warn("Validation failed for createTour. Errors count: {}", bindingResult.getErrorCount());
                bindingResult.getFieldErrors().forEach(error ->
                        logger.warn("Field: '{}' | Error: '{}' | Rejected value: '{}'",
                                error.getField(),
                                error.getDefaultMessage(),
                                error.getRejectedValue())
                );
            }
            else if (bindingResult != null && !bindingResult.hasErrors()) {
                logger.warn("BindingResult is empty.");
            }
        }
    }
}
