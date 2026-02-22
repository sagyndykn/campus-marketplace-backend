package com.campus.marketplace.service.impl;

import com.campus.marketplace.dto.request.CreateListingRequest;
import com.campus.marketplace.dto.request.UpdateListingRequest;
import com.campus.marketplace.dto.response.ListingResponse;
import com.campus.marketplace.enums.Category;
import com.campus.marketplace.enums.ListingStatus;
import com.campus.marketplace.model.Listing;
import com.campus.marketplace.model.User;
import com.campus.marketplace.repository.ListingRepository;
import com.campus.marketplace.repository.UserRepository;
import com.campus.marketplace.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public ListingResponse create(String sellerEmail, CreateListingRequest request) {
        User seller = getUser(sellerEmail);

        Listing listing = Listing.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .emoji(request.getEmoji())
                .sellerId(seller.getId())
                .sellerName(seller.getFirstName() + " " + seller.getLastName())
                .sellerAvatarUrl(seller.getAvatarUrl())
                .status(ListingStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toResponse(listingRepository.save(listing));
    }

    @Override
    public Page<ListingResponse> getFeed(String currentUserEmail,
                                         String category,
                                         String search,
                                         Double minPrice,
                                         Double maxPrice,
                                         int page,
                                         int size) {
        User currentUser = getUser(currentUserEmail);

        Criteria criteria = Criteria.where("sellerId").ne(currentUser.getId())
                .and("status").is(ListingStatus.ACTIVE);

        if (category != null && !category.isBlank()) {
            criteria = criteria.and("category").is(Category.valueOf(category));
        }
        if (search != null && !search.isBlank()) {
            criteria = criteria.and("title").regex(search, "i");
        }
        if (minPrice != null && maxPrice != null) {
            criteria = criteria.and("price").gte(minPrice).lte(maxPrice);
        } else if (minPrice != null) {
            criteria = criteria.and("price").gte(minPrice);
        } else if (maxPrice != null) {
            criteria = criteria.and("price").lte(maxPrice);
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Query query = new Query(criteria).with(pageable);
        Query countQuery = new Query(criteria);

        List<Listing> listings = mongoTemplate.find(query, Listing.class);
        long total = mongoTemplate.count(countQuery, Listing.class);

        List<ListingResponse> responses = listings.stream().map(this::toResponse).toList();
        return new PageImpl<>(responses, pageable, total);
    }

    @Override
    public ListingResponse getById(String id) {
        return toResponse(findListing(id));
    }

    @Override
    public ListingResponse update(String sellerEmail, String id, UpdateListingRequest request) {
        User seller = getUser(sellerEmail);
        Listing listing = findListing(id);

        if (!listing.getSellerId().equals(seller.getId())) {
            throw new RuntimeException("Нет доступа к этому объявлению");
        }

        if (request.getTitle() != null) listing.setTitle(request.getTitle());
        if (request.getDescription() != null) listing.setDescription(request.getDescription());
        if (request.getPrice() != null) listing.setPrice(request.getPrice());
        if (request.getCategory() != null) listing.setCategory(request.getCategory());
        if (request.getEmoji() != null) listing.setEmoji(request.getEmoji());
        if (request.getStatus() != null) listing.setStatus(request.getStatus());
        listing.setUpdatedAt(LocalDateTime.now());

        return toResponse(listingRepository.save(listing));
    }

    @Override
    public void delete(String sellerEmail, String id) {
        User seller = getUser(sellerEmail);
        Listing listing = findListing(id);

        if (!listing.getSellerId().equals(seller.getId())) {
            throw new RuntimeException("Нет доступа к этому объявлению");
        }

        listingRepository.delete(listing);
    }

    @Override
    public List<ListingResponse> getMyListings(String sellerEmail) {
        User seller = getUser(sellerEmail);
        return listingRepository.findBySellerIdOrderByCreatedAtDesc(seller.getId())
                .stream().map(this::toResponse).toList();
    }

    // --- helpers ---

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    private Listing findListing(String id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));
    }

    private ListingResponse toResponse(Listing listing) {
        return ListingResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .price(listing.getPrice())
                .category(listing.getCategory())
                .emoji(listing.getEmoji())
                .sellerId(listing.getSellerId())
                .sellerName(listing.getSellerName())
                .sellerAvatarUrl(listing.getSellerAvatarUrl())
                .status(listing.getStatus())
                .createdAt(listing.getCreatedAt())
                .build();
    }
}
