package com.qrio.admin.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.admin.dto.response.AdminDetailResponse;
import com.qrio.admin.dto.response.AdminListResponse;
import com.qrio.admin.model.Admin;
import com.qrio.shared.response.OptionResponse;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends CrudRepository<Admin, Long> {
    @Query("""
        SELECT 
            a.id AS id,
            a.name AS name,
            a.email AS email,
            a.phone AS phone,
            a.status AS status
        FROM Admin a
        ORDER BY a.id DESC
    """)
    List<AdminListResponse> findList();

    @Query("""
        SELECT 
            a.id AS id,
            a.name AS name,
            a.email AS email,
            a.phone AS phone,
            a.status AS status
        FROM Admin a
        WHERE a.id = :id
    """)
    Optional<AdminDetailResponse> findDetailById(Long id);

    @Query("""
        SELECT 
            a.id AS value,
            a.name AS label
        FROM Admin a
        WHERE a.status = 'ACTIVO'
        ORDER BY a.name
    """)
    List<OptionResponse> findForOptions();
}
