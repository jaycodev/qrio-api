package com.qrio.offer.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.offer.dto.response.OfferDetailResponse;
import com.qrio.offer.model.OfferProduct;

import java.util.List;

public interface OfferProductRepository extends CrudRepository<OfferProduct, Long> {
    @Query("""
        SELECT 
            op.product.id AS productId,
            op.quantity AS quantity
        FROM OfferProduct op
        WHERE op.offer.id = :offerId
        ORDER BY op.id
    """)
    List<OfferDetailResponse.OfferProductItem> findOfferProductsByOfferId(Long offerId);

    void deleteAllByOffer(com.qrio.offer.model.Offer offer);
}
