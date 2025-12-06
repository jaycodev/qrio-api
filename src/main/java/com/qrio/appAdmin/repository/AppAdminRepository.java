package com.qrio.appAdmin.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.appAdmin.dto.response.AppAdminDetailResponse;
import com.qrio.appAdmin.dto.response.AppAdminListResponse;
import com.qrio.appAdmin.model.AppAdmin;

import java.util.List;
import java.util.Optional;

public interface AppAdminRepository extends CrudRepository<AppAdmin, Long> {
    @Query("""
        SELECT 
            a.id AS id,
            a.name AS name,
            a.email AS email,
            a.createdAt AS createdAt
        FROM AppAdmin a
        ORDER BY a.id DESC
    """)
    List<AppAdminListResponse> findList();

    @Query("""
        SELECT 
            a.id AS id,
            a.name AS name,
            a.email AS email,
            a.createdAt AS createdAt
        FROM AppAdmin a
        WHERE a.id = :id
    """)
    Optional<AppAdminDetailResponse> findDetailById(Long id);
}
