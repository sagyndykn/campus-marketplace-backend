package com.campus.marketplace.dto.request;

import com.campus.marketplace.enums.Category;
import com.campus.marketplace.enums.ListingStatus;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateListingRequest {

    private String title;
    private String description;

    @Min(0)
    private Double price;

    private Category category;
    private String emoji;
    private ListingStatus status;
}
