package com.qrio.table.repository;

import com.qrio.shared.response.OptionResponse;
import com.qrio.table.model.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
    @Query("SELECT dt FROM DiningTable dt WHERE dt.branch.id = :branchId")
    List<DiningTable> findByBranchId(Long branchId);

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
