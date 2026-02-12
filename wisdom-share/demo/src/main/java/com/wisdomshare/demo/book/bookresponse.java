package com.wisdomshare.demo.book;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record bookresponse(
        Integer id,
        String title,
        String authorName,
        String isbn,
        String synopsis,
        String coverImageUrl,
        boolean shareable,
        boolean archived,    // <-- Ajouté pour corriger l'erreur dans le mapper
        double rate,         // <-- Ajouté pour corriger l'erreur .rate()
        Integer ownerId,
        String ownerFullName,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
}