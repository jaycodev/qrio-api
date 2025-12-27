package com.qrio.offer.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.offer.dto.request.CreateOfferProductRequest;
import com.qrio.offer.dto.request.CreateOfferRequest;
import com.qrio.offer.dto.request.UpdateOfferProductRequest;
import com.qrio.offer.dto.request.UpdateOfferRequest;
import com.qrio.offer.dto.response.OfferDetailResponse;
import com.qrio.offer.dto.response.OfferListResponse;
import com.qrio.offer.model.Offer;
import com.qrio.offer.model.OfferProduct;
import com.qrio.offer.repository.OfferProductRepository;
import com.qrio.offer.repository.OfferRepository;
import com.qrio.product.model.Product;
import com.qrio.product.repository.ProductRepository;
import com.qrio.restaurant.model.Restaurant;
import com.qrio.restaurant.repository.RestaurantRepository;
import com.qrio.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class OfferService {
    private final OfferRepository offerRepository;
    private final OfferProductRepository offerProductRepository;
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;

    public List<OfferListResponse> getList() {
        List<OfferListResponse> offers = offerRepository.findList();
        return offers.stream()
                .map(o -> new OfferListResponse(
                        o.id(),
                        o.code() != null ? o.code() : String.format("OF-%04d", o.id()),
                        o.restaurantId(),
                        o.title(),
                        o.description(),
                        o.offerDiscountPercentage(),
                        o.active()))
                .toList();
    }

    public OfferDetailResponse getDetailById(Long id) {
        OfferDetailResponse base = offerRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with ID: " + id));
        String code = base.code() != null ? base.code() : String.format("OF-%04d", base.id());
        OfferDetailResponse normalized = new OfferDetailResponse(
                base.id(),
                code,
                base.restaurantId(),
                base.title(),
                base.description(),
                base.offerDiscountPercentage(),
                base.active(),
                null);
        return normalized.withProducts(offerProductRepository.findOfferProductsByOfferId(id));
    }

    @Transactional
    public OfferListResponse create(CreateOfferRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Offer offer = new Offer();
        offer.setRestaurant(restaurant);
        offer.setTitle(request.title());
        offer.setDescription(request.description());
        offer.setOfferDiscountPercentage(request.offerDiscountPercentage());
        offer.setActive(request.active());

        Offer saved = offerRepository.save(offer);

        for (CreateOfferProductRequest productDto : request.products()) {
            Product product = productRepository.findById(productDto.productId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with ID: " + productDto.productId()));

            OfferProduct offerProduct = new OfferProduct();
            offerProduct.setOffer(saved);
            offerProduct.setProduct(product);
            offerProduct.setQuantity(productDto.quantity());

            offerProductRepository.save(offerProduct);
        }

        return toListResponse(saved);
    }

    @Transactional
    public OfferListResponse update(Long id, UpdateOfferRequest request) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with ID: " + id));

        offer.setTitle(request.title());
        offer.setDescription(request.description());
        offer.setOfferDiscountPercentage(request.offerDiscountPercentage());
        offer.setActive(request.active());

        Offer updated = offerRepository.save(offer);

        offerProductRepository.deleteAllByOffer(updated);

        for (UpdateOfferProductRequest productDto : request.products()) {
            Product product = productRepository.findById(productDto.productId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with ID: " + productDto.productId()));

            OfferProduct offerProduct = new OfferProduct();
            offerProduct.setOffer(updated);
            offerProduct.setProduct(product);
            offerProduct.setQuantity(productDto.quantity());

            offerProductRepository.save(offerProduct);
        }

        return toListResponse(updated);
    }

    public OfferListResponse toListResponse(Offer offer) {
        return new OfferListResponse(
                offer.getId(),
                offer.getCode(),
                offer.getRestaurant().getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getOfferDiscountPercentage(),
                offer.getActive());
    }

    @Transactional
    public void delete(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with ID: " + id));
        offerProductRepository.deleteAllByOffer(offer);
        offerRepository.delete(offer);
    }

    @Transactional
    public Boolean setActive(Long id, boolean active) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with ID: " + id));
        offer.setActive(active);
        Offer saved = offerRepository.save(offer);
        return saved.getActive();
    }

    @Transactional
    public Boolean toggleActive(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with ID: " + id));
        Boolean current = offer.getActive();
        if (current == null)
            current = false;
        Boolean newVal = !current;
        offer.setActive(newVal);
        Offer saved = offerRepository.save(offer);
        return saved.getActive();
    }
}
