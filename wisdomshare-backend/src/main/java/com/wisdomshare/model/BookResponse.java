package com.wisdomshare.model;

import java.util.List;

public record BookResponse(
    Integer id,
    String title,
    String authorName,
    String synopsis,
    String bookCover,
    boolean archived,
    boolean shareable,
    String owner,
    double rate,
    boolean rated,
    List<FeedbackResponse> feedbacks
) {
}