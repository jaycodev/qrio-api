package com.qrio.restaurant.service;

import com.qrio.restaurant.model.Restaurant;
import com.qrio.restaurant.repository.RestaurantRepository;
import com.qrio.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {
    private final RestaurantRepository repository;

    public RestaurantService(RestaurantRepository repository) {
        this.repository = repository;
    }

    public List<Restaurant> list() {
        return repository.findAll();
    }

    public Restaurant get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
    }

    public Restaurant create(Restaurant r) {
        return repository.save(r);
    }

    public Restaurant update(Long id, Restaurant data) {
        Restaurant current = get(id);
        current.setUserId(data.getUserId());
        current.setName(data.getName());
        current.setDescription(data.getDescription());
        current.setLogoUrl(data.getLogoUrl());
        return repository.save(current);
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }
}
