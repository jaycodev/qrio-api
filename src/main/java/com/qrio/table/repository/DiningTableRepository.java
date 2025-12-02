package com.qrio.table.repository;

import com.qrio.table.model.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
    @Query("SELECT dt FROM DiningTable dt WHERE dt.branch.id = :branchId")
    List<DiningTable> findByBranchId(Long branchId);

    Optional<DiningTable> findByQrCode(String qrCode);
}
