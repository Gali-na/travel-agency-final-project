package com.epam.travel_agency_final_project.repository;

import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.model.TourFilter;
import com.epam.travel_agency_final_project.repository.query.TourQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
@RequiredArgsConstructor
@Repository
public class TourRepositoryCustomImpl implements TourRepositoryCustom {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final String BASE_SQL_TOURS_WITH_FULL_INFO =TourQueries.BASE_SQL_TOURS_WITH_FULL_INFO;
    private static final String BASE_COUNT_SQL = TourQueries.BASE_COUNT_SQL;
    private static final String TORS_BY_ID_LANGUAGE = TourQueries.TORS_BY_ID_LANGUAGE;
    public Page<TourFullDTO> findToursWithJdbc(String lang, TourFilter filter, Pageable pageable) {
        StringBuilder sqlFullTours = new StringBuilder(BASE_SQL_TOURS_WITH_FULL_INFO);
        StringBuilder countSql = new StringBuilder(BASE_COUNT_SQL);
        Map<String, Object> params = new HashMap<>();
        params.put("lang", lang);

        if (filter.getIsHot() != null && filter.getIsHot()) {
            addTourIsHotCondition(sqlFullTours, countSql, params);
        }

        if (filter.getTourType() != null && !filter.getTourType().isEmpty()) {
            addTourTypeCondition(sqlFullTours, countSql, params, filter);
        }

        if (filter.getHotelType() != null && !filter.getHotelType().isEmpty()) {
            addHotelTypeCondition(sqlFullTours, countSql, params, filter);
        }
        sqlFullTours.append(" LIMIT :limit OFFSET :offset");
        params.put("limit", pageable.getPageSize());
        params.put("offset", pageable.getOffset());
        Long totalCount = getTotalCount(countSql.toString(), params);
        return executeQueryToursWithJdbc2(sqlFullTours.toString(), params, totalCount, pageable);

    }

    private void addTourIsHotCondition(StringBuilder sqlFullTours,
                                       StringBuilder countSql, Map<String, Object> params) {
        String cond = " AND t.is_hot = :isHot";
        sqlFullTours.append(cond);
        countSql.append(cond);
        params.put("isHot", true);
    }

    private void addTourTypeCondition(StringBuilder sqlFullTours,
                                      StringBuilder countSql,
                                      Map<String, Object> params, TourFilter filter) {
        String cond = " AND tr.tour_type = :tourType";
        sqlFullTours.append(cond);
        countSql.append(cond);
        params.put("tourType", filter.getTourType());
    }

    private void addHotelTypeCondition(StringBuilder sqlFullTours,
                                       StringBuilder countSql,
                                       Map<String, Object> params, TourFilter filter) {
        String targetHotel = filter.getHotelType().toLowerCase();
        String cond = " AND LOWER(tr.hotel_type) = :hotelType";
        sqlFullTours.append(cond);
        countSql.append(cond);
        params.put("hotelType", targetHotel.toLowerCase());
    }

    private long getTotalCount(String queryForTotalCount, Map<String, Object> params) {
        Long totalCount = jdbcTemplate.queryForObject(queryForTotalCount, params, Long.class);
        if (totalCount == null) {
            return totalCount = 0L;
        }
        return totalCount;
    }

    public Page<TourFullDTO> executeQueryToursWithJdbc2(String sql, Map<String, Object> params, Long totalCount, Pageable pageable) {
        List<TourFullDTO> tourFullDTOList =  mapQueryResultToTourFullDTO ( sql,  params);
        return new PageImpl<>(tourFullDTOList, pageable, totalCount);
    }

    @Override
    public List<TourFullDTO> findToursByIdsAndInLanguage(Set<UUID> ids, String lang) {
        if (ids == null || ids.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("lang", lang);
        params.put("ids", ids);
      return mapQueryResultToTourFullDTO (TORS_BY_ID_LANGUAGE, params);
    }
    private  List<TourFullDTO>  mapQueryResultToTourFullDTO (String sql, Map<String, Object> params){
        List<TourFullDTO> tourFullDTOList = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
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
        return tourFullDTOList;
    }
}
