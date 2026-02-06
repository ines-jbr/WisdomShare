package com.wisdomshare.demo.book;


import java.time.LocalDateTime;

public record bookresponse(
        Integer id,
        String title,
        String authorName,
        String isbn,
        String synopsis,
        String coverImageUrl,
        Boolean shareable,
        Integer ownerId,
        String ownerFullName,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {}