package com.qrio.branch.service;

import com.qrio.branch.model.Branch;
import com.qrio.branch.repository.BranchRepository;
import com.qrio.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {
    private final BranchRepository repository;

    public BranchService(BranchRepository repository) {
        this.repository = repository;
    }

    public List<Branch> list() {
        return repository.findAll();
    }

    public List<Branch> listByRestaurant(Long restaurantId) {
        return repository.findByRestaurantId(restaurantId);
    }

    public Branch get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
    }

    public Branch create(Branch b) {
        return repository.save(b);
    }

    public Branch update(Long id, Branch data) {
        Branch current = get(id);
        current.setRestaurantId(data.getRestaurantId());
        current.setName(data.getName());
        current.setAddress(data.getAddress());
        current.setPhone(data.getPhone());
        current.setSchedule(data.getSchedule());
        return repository.save(current);
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }
}
