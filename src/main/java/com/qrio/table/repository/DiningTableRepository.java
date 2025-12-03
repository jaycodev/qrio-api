package com.qrio.table.repository;

import com.qrio.shared.response.OptionResponse;
import com.qrio.table.dto.response.DiningTableDetailResponse;
import com.qrio.table.dto.response.DiningTableListResponse;
import com.qrio.table.model.DiningTable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DiningTableRepository extends CrudRepository<DiningTable, Long> {
    
    @Query("""
        SELECT 
            dt.id AS id,
            dt.branch.id AS branchId,
            dt.tableNumber AS tableNumber,
            dt.qrCode AS qrCode
        FROM DiningTable dt
        WHERE (:branchId IS NULL OR dt.branch.id = :branchId)
        ORDER BY dt.tableNumber ASC
    """)
    List<DiningTableListResponse> findList(Long branchId);

    @Query("""
        SELECT 
            dt.id AS id,
            dt.branch.id AS branchId,
            dt.tableNumber AS tableNumber,
            dt.qrCode AS qrCode
        FROM DiningTable dt
        WHERE dt.id = :id
    """)
    Optional<DiningTableDetailResponse> findDetailById(Long id);

    Optional<DiningTable> findByQrCode(String qrCode);

    @Query("""
        SELECT 
            dt.id AS value,
            CONCAT('Mesa ', CAST(dt.tableNumber AS STRING)) AS label
        FROM DiningTable dt 
        WHERE dt.branch.id = :branchId
        ORDER BY dt.tableNumber ASC
    """)
    List<OptionResponse> findForOptions(Long branchId);
}
