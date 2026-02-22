package com.campus.marketplace.repository;

import com.campus.marketplace.enums.ListingStatus;
import com.campus.marketplace.model.Listing;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ListingRepository extends MongoRepository<Listing, String> {

    List<Listing> findBySellerIdOrderByCreatedAtDesc(String sellerId);

    boolean existsByIdAndSellerId(String id, String sellerId);
}
