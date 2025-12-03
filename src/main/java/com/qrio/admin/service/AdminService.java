package com.qrio.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.admin.dto.request.CreateAdminRequest;
import com.qrio.admin.dto.request.UpdateAdminRequest;
import com.qrio.admin.dto.response.AdminDetailResponse;
import com.qrio.admin.dto.response.AdminListResponse;
import com.qrio.admin.model.Admin;
import com.qrio.shared.type.Status;
import com.qrio.admin.repository.AdminRepository;
import com.qrio.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class AdminService {
    private final AdminRepository adminRepository;

    public List<AdminListResponse> getList() {
        return adminRepository.findList();
    }

    public AdminDetailResponse getDetailById(Long id) {
        return adminRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + id));
    }

    @Transactional
    public AdminListResponse create(CreateAdminRequest request) {
        Admin admin = new Admin();
        admin.setName(request.name());
        admin.setEmail(request.email());
        admin.setPhone(request.phone());
        admin.setStatus(request.status() != null ? Status.valueOf(request.status()) : Status.ACTIVO);

        Admin saved = adminRepository.save(admin);
        return toListResponse(saved);
    }

    @Transactional
    public AdminListResponse update(Long id, UpdateAdminRequest request) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + id));

        admin.setName(request.name());
        admin.setEmail(request.email());
        admin.setPhone(request.phone());
        admin.setStatus(Status.valueOf(request.status()));

        Admin updated = adminRepository.save(admin);
        return toListResponse(updated);
    }

    private AdminListResponse toListResponse(Admin admin) {
        return new AdminListResponse(
                admin.getId(),
                admin.getName(),
                admin.getEmail(),
                admin.getPhone(),
                admin.getStatus());
    }
}
