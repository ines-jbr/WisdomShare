package com.wisdomshare.demo.feedback;

import com.wisdomshare.demo.common.baseentity;
import com.wisdomshare.demo.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class feedback extends baseentity {

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    // ────────────────────────────────────────────────────────────────
    // Optional / helpful fields
    // ────────────────────────────────────────────────────────────────

    @Column(length = 150)
    private String title;               // short summary / subject

    @Column(length = 50)
    private String category;            // e.g. "bug", "feature-request", "ui-ux", "content", "other"

    @Column
    private Integer rating;             // 1–5 (can be null if not rated)

    // Moderation / workflow field (very common in feedback systems)
    @Column(length = 20)
    @Builder.Default
    private String status = "PENDING";  // PENDING, REVIEWED, APPROVED, REJECTED, ARCHIVED

    // ────────────────────────────────────────────────────────────────
    // Helper methods (optional but useful)
    // ────────────────────────────────────────────────────────────────

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isApproved() {
        return "APPROVED".equals(status);
    }
}