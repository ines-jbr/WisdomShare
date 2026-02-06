package com.wisdomshare.demo.book;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record bookrequest(
        @NotNull(message = "ID is required for update") Integer id, // optional for create

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be less than 200 characters")
        String title,

        @NotBlank(message = "Author name is required")
        @Size(max = 150, message = "Author name must be less than 150 characters")
        String authorName,

        @NotBlank(message = "ISBN is required")
        @Size(min = 10, max = 20, message = "ISBN must be between 10 and 20 characters")
        String isbn,

        String synopsis,

        String coverImageUrl,

        Boolean shareable
) {}