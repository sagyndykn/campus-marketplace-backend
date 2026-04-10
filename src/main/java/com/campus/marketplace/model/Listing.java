package com.campus.marketplace.model;

import com.campus.marketplace.enums.Category;
import com.campus.marketplace.enums.ListingStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "listings")
public class Listing {

    @Id
    private String id;

    private String title;
    private String description;
    private Double price;
    private Category category;
    private String emoji;

    private String sellerId;
    private String sellerName;
    private String sellerAvatarUrl;

    @Builder.Default
    private List<String> photoUrls = new ArrayList<>();

    @Builder.Default
    private ListingStatus status = ListingStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}