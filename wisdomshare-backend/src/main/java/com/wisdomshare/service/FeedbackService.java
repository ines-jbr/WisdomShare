package com.wisdomshare.service;

import com.wisdomshare.entity.Book;
import com.wisdomshare.entity.Feedback;
import com.wisdomshare.model.FeedbackRequest;
import com.wisdomshare.repository.BookRepository;
import com.wisdomshare.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final BookRepository bookRepository;

    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + request.bookId()));

        Feedback feedback = Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(book)
                .build();

        return feedbackRepository.save(feedback).getId();
    }
}