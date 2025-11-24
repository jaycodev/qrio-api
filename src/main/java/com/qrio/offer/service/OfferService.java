package com.qrio.offer.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.offer.dto.request.CreateOfferRequest;
import com.qrio.offer.dto.request.UpdateOfferRequest;
import com.qrio.offer.dto.response.OfferDetailResponse;
import com.qrio.offer.dto.response.OfferListResponse;
import com.qrio.offer.model.Offer;
import com.qrio.offer.repository.OfferRepository;
import com.qrio.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class OfferService {
    private final OfferRepository offerRepository;

    public List<OfferListResponse> getList() {
        return offerRepository.findList();
    }

    public OfferDetailResponse getDetailById(Long id) {
        return offerRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with ID: " + id));
    }

    @Transactional
    public OfferListResponse create(CreateOfferRequest request) {
        Offer offer = new Offer();
        offer.setRestaurantId(request.restaurantId());
        offer.setTitle(request.title());
        offer.setDescription(request.description());
        offer.setOfferPrice(request.offerPrice());
        offer.setActive(request.active());
        offer.setCreatedAt(LocalDateTime.now());

        Offer saved = offerRepository.save(offer);

        return toListResponse(saved);
    }

    @Transactional
    public OfferListResponse update(Long id, UpdateOfferRequest request) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with ID: " + id));

        offer.setTitle(request.title());
        offer.setDescription(request.description());
        offer.setOfferPrice(request.offerPrice());
        offer.setActive(request.active());

        Offer updated = offerRepository.save(offer);

        return toListResponse(updated);
    }

    public OfferListResponse toListResponse(Offer offer) {
        return new OfferListResponse(
                offer.getId(),
                offer.getRestaurantId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getOfferPrice(),
                offer.getActive());
    }
}
