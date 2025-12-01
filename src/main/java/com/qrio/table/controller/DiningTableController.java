package com.qrio.table.controller;

import com.qrio.branch.model.Branch;
import com.qrio.branch.repository.BranchRepository;
import com.qrio.shared.api.ApiSuccess;
import com.qrio.shared.exception.ResourceNotFoundException;
import com.qrio.table.dto.request.CreateDiningTableRequest;
import com.qrio.table.dto.request.UpdateDiningTableRequest;
import com.qrio.table.dto.response.DiningTableResponse;
import com.qrio.table.model.DiningTable;
import com.qrio.table.service.DiningTableService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tables")
public class DiningTableController {
    private final DiningTableService service;
    private final BranchRepository branchRepository;

    public DiningTableController(DiningTableService service, BranchRepository branchRepository) {
        this.service = service;
        this.branchRepository = branchRepository;
    }

    @GetMapping
    public ResponseEntity<ApiSuccess<List<DiningTableResponse>>> list(
            @RequestParam(value = "branchId", required = false) Long branchId) {
        List<DiningTableResponse> data = (branchId == null ? service.list() : service.listByBranch(branchId))
                .stream().map(DiningTableResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiSuccess<>("Listado de mesas", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccess<DiningTableResponse>> get(@PathVariable Long id) {
        DiningTableResponse data = DiningTableResponse.from(service.get(id));
        return ResponseEntity.ok(new ApiSuccess<>("Detalle de mesa", data));
    }

    @PostMapping
    public ResponseEntity<ApiSuccess<DiningTableResponse>> create(
            @Validated @RequestBody CreateDiningTableRequest req) {
        Branch branch = branchRepository.findById(req.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        DiningTable t = new DiningTable();
        t.setBranch(branch);
        t.setTableNumber(req.getTableNumber());
        t.setQrCode(req.getQrCode());
        DiningTable saved = service.create(t);
        DiningTableResponse data = DiningTableResponse.from(saved);
        return ResponseEntity.created(URI.create("/api/tables/" + saved.getId()))
                .body(new ApiSuccess<>("Mesa creada", data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccess<DiningTableResponse>> update(@PathVariable Long id,
            @Validated @RequestBody UpdateDiningTableRequest req) {
        Branch branch = branchRepository.findById(req.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        DiningTable t = new DiningTable();
        t.setBranch(branch);
        t.setTableNumber(req.getTableNumber());
        t.setQrCode(req.getQrCode());
        DiningTable updated = service.update(id, t);
        return ResponseEntity.ok(new ApiSuccess<>("Mesa actualizada", DiningTableResponse.from(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccess<String>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(new ApiSuccess<>("Mesa eliminada", "deleted"));
    }
}
