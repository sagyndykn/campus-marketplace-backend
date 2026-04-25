package com.campus.marketplace.service;

import com.campus.marketplace.dto.request.CreateListingRequest;
import com.campus.marketplace.dto.request.UpdateListingRequest;
import com.campus.marketplace.dto.response.ListingResponse;
import org.springframework.data.domain.Page;


public interface ListingService {

    ListingResponse create(String sellerEmail, CreateListingRequest request);

    Page<ListingResponse> getFeed(String currentUserEmail,
                                  String category,
                                  String search,
                                  Double minPrice,
                                  Double maxPrice,
                                  int page,
                                  int size);

    ListingResponse getById(String id);

    ListingResponse update(String sellerEmail, String id, UpdateListingRequest request);

    void delete(String sellerEmail, String id);

    java.util.List<ListingResponse> getMyListings(String sellerEmail);

    ListingResponse addFavorite(String email, String listingId);

    void removeFavorite(String email, String listingId);

    java.util.List<ListingResponse> getFavorites(String email);
}
