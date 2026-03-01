package com.campus.marketplace.dto.response;

import com.campus.marketplace.enums.Category;
import com.campus.marketplace.enums.ListingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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

    private List<String> photoUrls;
    private ListingStatus status;
    private LocalDateTime createdAt;
}
