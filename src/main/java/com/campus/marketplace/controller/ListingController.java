package com.campus.marketplace.controller;

import com.campus.marketplace.dto.request.CreateListingRequest;
import com.campus.marketplace.dto.request.UpdateListingRequest;
import com.campus.marketplace.dto.response.ListingResponse;
import com.campus.marketplace.service.ListingService;
import com.campus.marketplace.service.impl.ListingServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;
    private final ListingServiceImpl listingServiceImpl;

    @PostMapping
    public ResponseEntity<ListingResponse> create(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody CreateListingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(listingService.create(email, request));
    }

    @GetMapping
    public ResponseEntity<Page<ListingResponse>> getFeed(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String sellerId,
            @RequestParam(required = false) String excludeId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String resolvedCategory = category != null && !category.isBlank() ? category : categoryId;
        return ResponseEntity.ok(
                listingService.getFeed(email, resolvedCategory, sellerId, excludeId, search, minPrice, maxPrice, page, size));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ListingResponse>> getMyListings(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(listingService.getMyListings(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(listingService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListingResponse> update(
            @AuthenticationPrincipal String email,
            @PathVariable String id,
            @Valid @RequestBody UpdateListingRequest request) {
        return ResponseEntity.ok(listingService.update(email, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal String email,
            @PathVariable String id) {
        listingService.delete(email, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<ListingResponse>> getFavorites(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(listingService.getFavorites(email));
    }

    @DeleteMapping("/favorites")
    public ResponseEntity<Void> clearFavorites(@AuthenticationPrincipal String email) {
        listingService.clearFavorites(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<ListingResponse> addFavorite(
            @AuthenticationPrincipal String email, @PathVariable String id) {
        return ResponseEntity.ok(listingService.addFavorite(email, id));
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal String email, @PathVariable String id) {
        listingService.removeFavorite(email, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/photos")
    public ResponseEntity<ListingResponse> uploadPhotos(
            @AuthenticationPrincipal String email,
            @PathVariable String id,
            @RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(listingServiceImpl.uploadPhotos(email, id, files));
    }

    @DeleteMapping("/{id}/photos")
    public ResponseEntity<ListingResponse> deletePhoto(
            @AuthenticationPrincipal String email,
            @PathVariable String id,
            @RequestParam("url") String photoUrl) {
        return ResponseEntity.ok(listingServiceImpl.deletePhoto(email, id, photoUrl));
    }
}
