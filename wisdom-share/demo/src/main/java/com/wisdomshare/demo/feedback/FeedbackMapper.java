package com.wisdomshare.demo.feedback;

import com.wisdomshare.demo.book.book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {

    public feedback toFeedback(FeedbackRequest request) {
        book bookEntity = new book();
        bookEntity.setId(request.bookId()); 

        return feedback.builder()
                .rating(request.note() != null ? request.note().intValue() : 0)
                .message(request.comment())
                .book(bookEntity)
                .build();
    }

    public FeedbackResponse toFeedbackResponse(feedback f, Integer userId) {
        return FeedbackResponse.builder()
                .note(f.getRating() != null ? f.getRating().doubleValue() : 0.0) 
                .comment(f.getMessage())
                .ownFeedback(f.getUser() != null && Objects.equals(f.getUser().getId(), userId))
                .build();
    }
}