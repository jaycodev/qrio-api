package com.qrio.table.service;

import com.qrio.branch.model.Branch;
import com.qrio.branch.repository.BranchRepository;
import com.qrio.shared.exception.ResourceNotFoundException;
import com.qrio.table.dto.request.CreateDiningTableRequest;
import com.qrio.table.dto.request.UpdateDiningTableRequest;
import com.qrio.table.dto.response.DiningTableDetailResponse;
import com.qrio.table.dto.response.DiningTableListResponse;
import com.qrio.table.model.DiningTable;
import com.qrio.table.repository.DiningTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class DiningTableService {
    private final DiningTableRepository diningTableRepository;
    private final BranchRepository branchRepository;

    @Transactional(readOnly = true)
    public List<DiningTableListResponse> getList(Long branchId) {
        return diningTableRepository.findList(branchId);
    }

    @Transactional(readOnly = true)
    public DiningTableDetailResponse getDetailById(Long id) {
        return diningTableRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dining table not found"));
    }

    @Transactional
    public DiningTableListResponse create(CreateDiningTableRequest request) {
        Branch branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        DiningTable table = new DiningTable();
        table.setBranch(branch);
        table.setTableNumber(request.tableNumber());
        table.setQrCode(request.qrCode());

        DiningTable saved = diningTableRepository.save(table);
        return toListResponse(saved);
    }

    @Transactional
    public DiningTableListResponse update(Long id, UpdateDiningTableRequest request) {
        DiningTable table = diningTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dining table not found"));

        Branch branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        table.setBranch(branch);
        table.setTableNumber(request.tableNumber());
        table.setQrCode(request.qrCode());

        DiningTable updated = diningTableRepository.save(table);
        return toListResponse(updated);
    }

    private DiningTableListResponse toListResponse(DiningTable table) {
        return new DiningTableListResponse(
                table.getId(),
                table.getBranch().getId(),
                table.getTableNumber(),
                table.getQrCode());
    }
}
