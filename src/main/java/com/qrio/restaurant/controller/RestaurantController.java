package com.qrio.restaurant.controller;

import com.qrio.restaurant.dto.request.CreateRestaurantRequest;
import com.qrio.restaurant.dto.request.UpdateRestaurantRequest;
import com.qrio.restaurant.dto.response.RestaurantResponse;
import com.qrio.restaurant.model.Restaurant;
import com.qrio.restaurant.service.RestaurantService;
import com.qrio.shared.api.ApiSuccess;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiSuccess<List<RestaurantResponse>>> list() {
        List<RestaurantResponse> data = service.list().stream().map(RestaurantResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiSuccess<>("Listado de restaurantes", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccess<RestaurantResponse>> get(@PathVariable Long id) {
        RestaurantResponse data = RestaurantResponse.from(service.get(id));
        return ResponseEntity.ok(new ApiSuccess<>("Detalle de restaurante", data));
    }

    @PostMapping
    public ResponseEntity<ApiSuccess<RestaurantResponse>> create(@Validated @RequestBody CreateRestaurantRequest req) {
        Restaurant r = new Restaurant();
        r.setUserId(req.getUserId());
        r.setName(req.getName());
        r.setDescription(req.getDescription());
        r.setLogoUrl(req.getLogoUrl());
        Restaurant saved = service.create(r);
        RestaurantResponse data = RestaurantResponse.from(saved);
        return ResponseEntity.created(URI.create("/api/restaurants/" + saved.getId()))
                .body(new ApiSuccess<>("Restaurante creado", data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccess<RestaurantResponse>> update(@PathVariable Long id,
            @Validated @RequestBody UpdateRestaurantRequest req) {
        Restaurant r = new Restaurant();
        r.setUserId(req.getUserId());
        r.setName(req.getName());
        r.setDescription(req.getDescription());
        r.setLogoUrl(req.getLogoUrl());
        Restaurant updated = service.update(id, r);
        return ResponseEntity.ok(new ApiSuccess<>("Restaurante actualizado", RestaurantResponse.from(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccess<String>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(new ApiSuccess<>("Restaurante eliminado", "deleted"));
    }
}
