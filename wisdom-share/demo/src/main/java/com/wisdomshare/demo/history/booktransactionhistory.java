package com.wisdomshare.demo.history;

import com.wisdomshare.demo.common.baseentity;
import com.wisdomshare.demo.book.book;
import com.wisdomshare.demo.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "book_transaction_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class booktransactionhistory extends baseentity {

    // ────────────────────────────────────────────────────────────────
    // Relationships
    // ────────────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private book book;

    // ────────────────────────────────────────────────────────────────
    // Core transaction fields
    // ────────────────────────────────────────────────────────────────

    /**
     * Date when the book was borrowed / reserved
     */
    @Column(nullable = false)
    private LocalDate borrowDate;

    /**
     * Expected return date (due date)
     */
    @Column(nullable = false)
    private LocalDate returnDate;

    /**
     * Actual date when the book was returned
     * null = book is still borrowed / not returned yet
     */
    @Column
    private LocalDate actualReturnDate;

    // ────────────────────────────────────────────────────────────────
    // Status & flags
    // ────────────────────────────────────────────────────────────────

    /**
     * Current status of the transaction
     */
    @Column(length = 30, nullable = false)
    @Builder.Default
    private String status = "BORROWED"; // BORROWED, RETURNED, OVERDUE, LOST, CANCELLED, RESERVED

    /**
     * Whether the book was returned on time
     * (calculated or set when actualReturnDate is filled)
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean returnedOnTime = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean returned = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean returnApproved = false;

    /**
     * Whether the transaction is currently active
     * (false = transaction is archived / completed)
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    // ────────────────────────────────────────────────────────────────
    // Optional extra fields (common in library systems)
    // ────────────────────────────────────────────────────────────────

    /**
     * Number of days the book is overdue (calculated)
     */
    @Column
    private Integer overdueDays;

    /**
     * Fine amount if overdue (can be calculated or stored)
     */
    @Column(precision = 10, scale = 2)
    private Double fineAmount;

    /**
     * Notes / admin comments (damaged book, etc.)
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    // ────────────────────────────────────────────────────────────────
    // Helper / convenience methods
    // ────────────────────────────────────────────────────────────────

    public boolean isReturned() {
        return actualReturnDate != null;
    }

    public boolean isOverdue() {
        return status.equals("OVERDUE") ||
                (actualReturnDate == null && LocalDate.now().isAfter(returnDate));
    }
}