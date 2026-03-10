package com.wisdomshare.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record FeedbackRequest(
    @NotNull(message = "Note is mandatory")
    @Min(value = 0, message = "Note must be greater than or equal to 0")
    @Max(value = 5, message = "Note must be less than or equal to 5")
    Double note,
    @NotNull(message = "Comment is mandatory")
    @NotEmpty(message = "Comment is mandatory")
    @NotBlank(message = "Comment is mandatory")
    String comment,
    @NotNull(message = "Book id is mandatory")
    Integer bookId
) {
}