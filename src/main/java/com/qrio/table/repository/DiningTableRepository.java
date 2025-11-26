package com.qrio.table.repository;

import com.qrio.table.model.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
    List<DiningTable> findByBranchId(Long branchId);

    Optional<DiningTable> findByQrCode(String qrCode);
}
