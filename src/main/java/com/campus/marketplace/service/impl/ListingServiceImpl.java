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
import com.campus.marketplace.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final MinioService minioService;

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
                                         String sellerId,
                                         String excludeId,
                                         String search,
                                         Double minPrice,
                                         Double maxPrice,
                                         int page,
                                         int size) {
        User currentUser = getUser(currentUserEmail);

        Criteria criteria = Criteria.where("status").is(ListingStatus.ACTIVE);

        if (sellerId != null && !sellerId.isBlank()) {
            criteria = criteria.and("sellerId").is(sellerId);
        } else {
            criteria = criteria.and("sellerId").ne(currentUser.getId());
        }

        if (excludeId != null && !excludeId.isBlank()) {
            criteria = criteria.and("_id").ne(excludeId);
        }

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

        Set<String> favIds = currentUser.getFavoriteListingIds();
        List<ListingResponse> responses = listings.stream().map(l -> toResponse(l, favIds)).toList();
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
        Set<String> favIds = seller.getFavoriteListingIds();
        return listingRepository.findBySellerIdOrderByCreatedAtDesc(seller.getId())
                .stream().map(l -> toResponse(l, favIds)).toList();
    }

    @Override
    public ListingResponse addFavorite(String email, String listingId) {
        findListing(listingId);
        mongoTemplate.updateFirst(
                new Query(Criteria.where("email").is(email)),
                new Update().addToSet("favoriteListingIds", listingId),
                User.class);
        User updated = getUser(email);
        return toResponse(findListing(listingId), updated.getFavoriteListingIds());
    }

    @Override
    public void removeFavorite(String email, String listingId) {
        mongoTemplate.updateFirst(
                new Query(Criteria.where("email").is(email)),
                new Update().pull("favoriteListingIds", listingId),
                User.class);
    }

    @Override
    public void clearFavorites(String email) {
        mongoTemplate.updateFirst(
                new Query(Criteria.where("email").is(email)),
                new Update().set("favoriteListingIds", new java.util.HashSet<>()),
                User.class);
    }

    @Override
    public List<ListingResponse> getFavorites(String email) {
        User user = getUser(email);
        Set<String> favIds = user.getFavoriteListingIds();
        if (favIds.isEmpty()) return new ArrayList<>();
        return listingRepository.findAllById(favIds)
                .stream().map(l -> toResponse(l, favIds)).toList();
    }

    public ListingResponse uploadPhotos(String sellerEmail, String id, List<MultipartFile> files) {
        User seller = getUser(sellerEmail);
        Listing listing = findListing(id);

        if (!listing.getSellerId().equals(seller.getId())) {
            throw new RuntimeException("Нет доступа к этому объявлению");
        }

        List<String> existing = listing.getPhotoUrls() == null ? new ArrayList<>() : listing.getPhotoUrls();
        if (existing.size() + files.size() > 5) {
            throw new RuntimeException("Максимум 5 фото на объявление (уже загружено: " + existing.size() + ")");
        }

        List<String> newUrls = files.stream().map(f -> minioService.upload(f, "listings")).toList();
        existing.addAll(newUrls);
        listing.setPhotoUrls(existing);
        listing.setUpdatedAt(LocalDateTime.now());

        return toResponse(listingRepository.save(listing));
    }

    public ListingResponse deletePhoto(String sellerEmail, String id, String photoUrl) {
        User seller = getUser(sellerEmail);
        Listing listing = findListing(id);

        if (!listing.getSellerId().equals(seller.getId())) {
            throw new RuntimeException("Нет доступа к этому объявлению");
        }

        List<String> photos = new ArrayList<>(listing.getPhotoUrls());
        if (!photos.remove(photoUrl)) {
            throw new RuntimeException("Фото не найдено");
        }

        minioService.delete(photoUrl);
        listing.setPhotoUrls(photos);
        listing.setUpdatedAt(LocalDateTime.now());

        return toResponse(listingRepository.save(listing));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    private Listing findListing(String id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));
    }

    private ListingResponse toResponse(Listing listing) {
        return toResponse(listing, Set.of());
    }

    private ListingResponse toResponse(Listing listing, Set<String> favIds) {
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
                .photoUrls(listing.getPhotoUrls() != null ? listing.getPhotoUrls() : new ArrayList<>())
                .status(listing.getStatus())
                .createdAt(listing.getCreatedAt())
                .favorited(favIds != null && favIds.contains(listing.getId()))
                .build();
    }
}
