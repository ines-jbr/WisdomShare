package com.wisdomshare.model;

public record FeedbackResponse(
    Double note,
    String comment,
    boolean ownFeedback
) {
}