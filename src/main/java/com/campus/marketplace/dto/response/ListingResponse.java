package com.campus.marketplace.dto.response;

import com.campus.marketplace.enums.Category;
import com.campus.marketplace.enums.ListingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ListingResponse {

    private String id;
    private String title;
    private String description;
    private Double price;
    private Category category;
    private String emoji;

    private String sellerId;
    private String sellerName;
    private String sellerAvatarUrl;

    private ListingStatus status;
    private LocalDateTime createdAt;
}
