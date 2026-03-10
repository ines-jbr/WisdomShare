package com.wisdomshare.model;

import java.util.List;

public record PageResponseBookResponse(
    List<BookResponse> content,
    int number,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
}