package com.campus.marketplace.dto.request;

import com.campus.marketplace.enums.Category;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateListingRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    @Min(0)
    private Double price;

    @NotNull
    private Category category;

    @NotBlank
    private String emoji;
}
