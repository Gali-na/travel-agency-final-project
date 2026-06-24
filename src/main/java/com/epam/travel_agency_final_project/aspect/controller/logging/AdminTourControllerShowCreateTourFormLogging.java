package com.epam.travel_agency_final_project.aspect.controller.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AdminTourControllerShowCreateTourFormLogging {
    private static final Logger logger = LogManager.getLogger(AdminTourControllerShowCreateTourFormLogging.class);
    @Before("execution(* com.epam.travel_agency_final_project.controller.*.showCreateTourForm(..))")
    public void logBefore() {
        logger.info("Method showCreateTourForm was invoked.");
    }
}
