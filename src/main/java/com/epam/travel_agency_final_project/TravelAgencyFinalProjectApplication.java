package com.epam.travel_agency_final_project;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TravelAgencyFinalProjectApplication {
	private static final Logger logger = LogManager.getLogger(TravelAgencyFinalProjectApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(TravelAgencyFinalProjectApplication.class, args);

		logger.info("==========================================");
		logger.info("Application started successfully!");
		logger.info("==========================================");

	}

}
