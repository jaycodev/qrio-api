package com.qrio.appAdmin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.appAdmin.dto.request.CreateAppAdminRequest;
import com.qrio.appAdmin.dto.request.UpdateAppAdminRequest;
import com.qrio.appAdmin.dto.response.AppAdminDetailResponse;
import com.qrio.appAdmin.dto.response.AppAdminListResponse;
import com.qrio.appAdmin.model.AppAdmin;
import com.qrio.appAdmin.repository.AppAdminRepository;
import com.qrio.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class AppAdminService {
    private final AppAdminRepository appAdminRepository;

    public List<AppAdminListResponse> getList() {
        return appAdminRepository.findList();
    }

    public AppAdminDetailResponse getDetailById(Long id) {
        return appAdminRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AppAdmin not found with ID: " + id));
    }

    @Transactional
    public AppAdminListResponse create(CreateAppAdminRequest request) {
        AppAdmin appAdmin = new AppAdmin();
        appAdmin.setName(request.name());
        appAdmin.setEmail(request.email());
        appAdmin.setPassword(request.password());

        AppAdmin saved = appAdminRepository.save(appAdmin);
        return toListResponse(saved);
    }

    @Transactional
    public AppAdminListResponse update(Long id, UpdateAppAdminRequest request) {
        AppAdmin appAdmin = appAdminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AppAdmin not found with ID: " + id));

        appAdmin.setName(request.name());
        appAdmin.setEmail(request.email());
        appAdmin.setPassword(request.password());

        AppAdmin updated = appAdminRepository.save(appAdmin);
        return toListResponse(updated);
    }

    private AppAdminListResponse toListResponse(AppAdmin appAdmin) {
        return new AppAdminListResponse(
                appAdmin.getId(),
                appAdmin.getName(),
                appAdmin.getEmail(),
                appAdmin.getCreatedAt());
    }
}
