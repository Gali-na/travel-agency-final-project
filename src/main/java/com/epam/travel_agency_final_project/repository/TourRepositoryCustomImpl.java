package com.epam.travel_agency_final_project.repository;


import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.model.TourFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

public class TourRepositoryCustomImpl implements TourRepositoryCustom {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private static final String BASE_SQL =
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
    private static final String BASE_COUNT_SQL =
            "SELECT COUNT(DISTINCT t.id) FROM tours t " +
                    "JOIN tours_translations tr ON t.id = tr.tours_id " +
                    "WHERE LOWER(tr.lang) = LOWER(:lang)";

    @Override
    public Page<TourFullDTO> findToursWithJdbc(
            String lang,
            TourFilter filter,
            Pageable pageable
    ) {
        StringBuilder sql = new StringBuilder(BASE_SQL);
        StringBuilder countSql = new StringBuilder(BASE_COUNT_SQL);
        Map<String, Object> params = new HashMap<>();
        params.put("lang", lang);

        if (filter.getIsHot() != null && filter.getIsHot()) {
            String cond = " AND t.is_hot = :isHot";
            sql.append(cond);
            countSql.append(cond);
            params.put("isHot", true);
        }

        if (filter.getTourType() != null && !filter.getTourType().isEmpty()) {
            String cond = " AND tr.tour_type = :tourType";
            sql.append(cond);
            countSql.append(cond);
            params.put("tourType", filter.getTourType());
        }
        if (filter.getHotelType() != null && !filter.getHotelType().isEmpty()) {
            String targetHotel = filter.getHotelType().toLowerCase();
            String cond = " AND LOWER(tr.hotel_type) = :hotelType";
            sql.append(cond);
            countSql.append(cond);
            params.put("hotelType", targetHotel);
        }
        sql.append(" LIMIT :limit OFFSET :offset");
        params.put("limit", pageable.getPageSize());
        params.put("offset", pageable.getOffset());

        Long totalCount = jdbcTemplate.queryForObject(
                countSql.toString(), params, Long.class
        );
        if (totalCount == null) totalCount = 0L;

        List<TourFullDTO> dtos = jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            TourFullDTO dto = new TourFullDTO();
            dto.setId(UUID.fromString(rs.getString("tour_id")));
            dto.setPrice(rs.getBigDecimal("price"));
            dto.setCityId(UUID.fromString(rs.getString("city_id")));
            if (rs.getTimestamp("arrival_date") != null) {
                dto.setArrivalDate(rs.getTimestamp("arrival_date").toLocalDateTime());
            }
            if (rs.getTimestamp("eviction_date") != null) {
                dto.setEvictionDate(rs.getTimestamp("eviction_date").toLocalDateTime());
            }

            dto.setHot(rs.getBoolean("is_hot"));
            dto.setImagePath(rs.getString("image_path"));
            dto.setTitle(rs.getString("title"));
            dto.setDescription(rs.getString("description"));
            dto.setTourType(rs.getString("tour_type"));
            dto.setTransferType(rs.getString("transfer_type"));
            dto.setHotelType(rs.getString("hotel_type"));
            dto.setCityName(rs.getString("city_name"));
            return dto;
        });

        return new PageImpl<>(dtos, pageable, totalCount);
    }

    @Override
    public List<TourFullDTO> findToursByIdsAndInLanguage(Set<UUID> ids, String lang) {
        if (ids == null || ids.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        String sql =
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
                        "AND t.id IN (:ids)"; // Витягуємо тільки потрібні тури одним запитом!

        Map<String, Object> params = new HashMap<>();
        params.put("lang", lang);
        params.put("ids", ids);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            TourFullDTO dto = new TourFullDTO();
            dto.setId(UUID.fromString(rs.getString("tour_id")));
            dto.setPrice(rs.getBigDecimal("price"));
            dto.setCityId(UUID.fromString(rs.getString("city_id")));
            dto.setImagePath(rs.getString("image_path"));
            dto.setTitle(rs.getString("title"));
            dto.setDescription(rs.getString("description"));
            dto.setTourType(rs.getString("tour_type"));
            dto.setTransferType(rs.getString("transfer_type"));
            dto.setHotelType(rs.getString("hotel_type"));
            dto.setCityName(rs.getString("city_name"));

            if (rs.getTimestamp("arrival_date") != null) {
                dto.setArrivalDate(rs.getTimestamp("arrival_date").toLocalDateTime());
            }
            if (rs.getTimestamp("eviction_date") != null) {
                dto.setEvictionDate(rs.getTimestamp("eviction_date").toLocalDateTime());
            }
            return dto;
        });
    }
}
