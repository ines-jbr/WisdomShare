package com.wisdomshare.demo.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Remplace strictement Feedback par feedback (minuscule)
public interface FeedbackRepository extends JpaRepository<feedback, Integer> {

    @Query("""
            SELECT f
            FROM feedback f
            WHERE f.book.id = :bookId
            """)
    Page<feedback> findAllByBookId(@Param("bookId") Integer bookId, Pageable pageable);
}