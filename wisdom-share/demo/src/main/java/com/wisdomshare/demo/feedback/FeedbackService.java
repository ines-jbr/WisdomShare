package com.wisdomshare.demo.feedback;

import com.wisdomshare.demo.book.book;
import com.wisdomshare.demo.book.bookrepo;
import com.wisdomshare.demo.common.pageresponse;
import com.wisdomshare.demo.exception.operationnotpermittedexception;
import com.wisdomshare.demo.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final bookrepo bookRepository; // Utilisation du nom correct de l'import
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        // Remplace request.bookId() par request.getBookId()
        book book = bookRepository.findById(request.bookId())
        .orElseThrow(() -> new EntityNotFoundException("No book found with the ID:: " + request.bookId()));
        
        if (book.isArchived() || !book.isShareable()) {
            throw new operationnotpermittedexception("You cannot give a feedback for an archived or not shareable book");
        }

        User user = ((User) connectedUser.getPrincipal());
        
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new operationnotpermittedexception("You cannot give a feedback to your own book");
        }

        feedback feedback = feedbackMapper.toFeedback(request);
        return feedbackRepository.save(feedback).getId();
    }

    public pageresponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        User user = ((User) connectedUser.getPrincipal());
        Page<feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
        
        List<FeedbackResponse> feedbackResponses = feedbacks.getContent()
                .stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        
        return new pageresponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}