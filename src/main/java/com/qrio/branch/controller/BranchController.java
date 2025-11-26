package com.qrio.branch.controller;

import com.qrio.branch.dto.request.CreateBranchRequest;
import com.qrio.branch.dto.request.UpdateBranchRequest;
import com.qrio.branch.dto.response.BranchResponse;
import com.qrio.branch.model.Branch;
import com.qrio.branch.service.BranchService;
import com.qrio.shared.api.ApiSuccess;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/branches")
public class BranchController {
    private final BranchService service;

    public BranchController(BranchService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiSuccess<List<BranchResponse>>> list(
            @RequestParam(value = "restaurantId", required = false) Long restaurantId) {
        List<BranchResponse> data = (restaurantId == null ? service.list() : service.listByRestaurant(restaurantId))
                .stream().map(BranchResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiSuccess<>("Listado de sucursales", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccess<BranchResponse>> get(@PathVariable Long id) {
        BranchResponse data = BranchResponse.from(service.get(id));
        return ResponseEntity.ok(new ApiSuccess<>("Detalle de sucursal", data));
    }

    @PostMapping
    public ResponseEntity<ApiSuccess<BranchResponse>> create(@Validated @RequestBody CreateBranchRequest req) {
        Branch b = new Branch();
        b.setRestaurantId(req.getRestaurantId());
        b.setName(req.getName());
        b.setAddress(req.getAddress());
        b.setPhone(req.getPhone());
        b.setSchedule(req.getSchedule());
        Branch saved = service.create(b);
        BranchResponse data = BranchResponse.from(saved);
        return ResponseEntity.created(URI.create("/api/branches/" + saved.getId()))
                .body(new ApiSuccess<>("Sucursal creada", data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccess<BranchResponse>> update(@PathVariable Long id,
            @Validated @RequestBody UpdateBranchRequest req) {
        Branch b = new Branch();
        b.setRestaurantId(req.getRestaurantId());
        b.setName(req.getName());
        b.setAddress(req.getAddress());
        b.setPhone(req.getPhone());
        b.setSchedule(req.getSchedule());
        Branch updated = service.update(id, b);
        return ResponseEntity.ok(new ApiSuccess<>("Sucursal actualizada", BranchResponse.from(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccess<String>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(new ApiSuccess<>("Sucursal eliminada", "deleted"));
    }
}
