package com.wisdomshare.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
    @NotNull(message = "Title is mandatory")
    @NotEmpty(message = "Title is mandatory")
    String title,
    @NotNull(message = "Author name is mandatory")
    @NotEmpty(message = "Author name is mandatory")
    String authorName,
    @NotNull(message = "Synopsis is mandatory")
    @NotEmpty(message = "Synopsis is mandatory")
    String synopsis,
    @NotNull(message = "Shareable is mandatory")
    Boolean shareable
) {
}