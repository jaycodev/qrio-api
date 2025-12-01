package com.qrio.table.service;

import com.qrio.shared.exception.ResourceNotFoundException;
import com.qrio.table.model.DiningTable;
import com.qrio.table.repository.DiningTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiningTableService {
    private final DiningTableRepository repository;

    public DiningTableService(DiningTableRepository repository) {
        this.repository = repository;
    }

    public List<DiningTable> list() {
        return repository.findAll();
    }

    public List<DiningTable> listByBranch(Long branchId) {
        return repository.findByBranchId(branchId);
    }

    public DiningTable get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dining table not found"));
    }

    public DiningTable create(DiningTable t) {
        return repository.save(t);
    }

    public DiningTable update(Long id, DiningTable data) {
        DiningTable current = get(id);
        if (data.getBranch() != null) {
            current.setBranch(data.getBranch());
        }
        current.setTableNumber(data.getTableNumber());
        current.setQrCode(data.getQrCode());
        return repository.save(current);
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }
}
