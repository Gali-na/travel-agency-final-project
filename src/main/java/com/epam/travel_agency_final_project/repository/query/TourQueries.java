package com.epam.travel_agency_final_project.repository.query;

public final class TourQueries {
   public static final String BASE_SQL_TOURS_WITH_FULL_INFO =
            "SELECT t.id AS tour_id, t.price, t.arrival_date, " +
                    "t.eviction_date, t.is_hot, t.image_path, t.city_id, " +
                    "tr.title, tr.description, tr.tour_type, " +
                    "tr.transfer_type, tr.hotel_type, " +
                    "ct.name AS city_name " +
                    "FROM tours t " +
                    "JOIN tours_translations tr ON t.id = tr.tours_id " +
                    "LEFT JOIN city_translations ct ON t.city_id = ct.city_id " +
                    "AND LOWER(ct.lang) = LOWER(:lang) " +
                    "WHERE LOWER(tr.lang) = LOWER(:lang)";
    public static final String BASE_COUNT_SQL =
            "SELECT COUNT(DISTINCT t.id) FROM tours t " +
                    "JOIN tours_translations tr ON t.id = tr.tours_id " +
                    "WHERE LOWER(tr.lang) = LOWER(:lang)";


    public static final String TORS_BY_ID_LANGUAGE =
            "SELECT t.id AS tour_id, t.price, t.arrival_date, " +
                    "t.eviction_date, t.is_hot, t.image_path, t.city_id, " +
                    "tr.title, tr.description, tr.tour_type, " +
                    "tr.transfer_type, tr.hotel_type, " +
                    "ct.name AS city_name " +
                    "FROM tours t " +
                    "JOIN tours_translations tr ON t.id = tr.tours_id " +
                    "LEFT JOIN city_translations ct ON t.city_id = ct.city_id " +
                    "AND LOWER(ct.lang) = LOWER(:lang) " +
                    "WHERE LOWER(tr.lang) = LOWER(:lang) " +
                    "AND t.id IN (:ids)";

}
