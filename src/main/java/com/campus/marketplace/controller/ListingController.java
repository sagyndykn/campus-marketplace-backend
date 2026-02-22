package com.campus.marketplace.controller;

import com.campus.marketplace.dto.request.CreateListingRequest;
import com.campus.marketplace.dto.request.UpdateListingRequest;
import com.campus.marketplace.dto.response.ListingResponse;
import com.campus.marketplace.service.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

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
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                listingService.getFeed(email, category, search, minPrice, maxPrice, page, size));
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
}
