package com.epam.travel_agency_final_project.repository;

import com.epam.travel_agency_final_project.entity.RoleEntity;
import com.epam.travel_agency_final_project.model.Role; // Ваш Enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    List<RoleEntity> findByNameIn(List<Role> names);
}
