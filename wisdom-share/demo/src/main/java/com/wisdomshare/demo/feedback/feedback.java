package com.wisdomshare.demo.feedback;

import com.wisdomshare.demo.book.book;
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
    @JoinColumn(name = "book_id", nullable = false)
    private book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    // Champs optionnels mais très fréquents
    @Column(length = 150)
    private String title;

    @Column(length = 50)
    private String category;

    @Column(nullable = false)
    private Integer rating;         // 1 à 5

    @Column(length = 20)
    @Builder.Default
    private String status = "PENDING";  // PENDING, REVIEWED, APPROVED, REJECTED, ARCHIVED

    // ────────────────────────────────────────────────
    // Méthodes métier utiles
    // ────────────────────────────────────────────────

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isApproved() {
        return "APPROVED".equals(status);
    }

    public boolean isRejected() {
        return "REJECTED".equals(status);
    }

    public boolean hasRating() {
        return rating != null && rating >= 1 && rating <= 5;
    }

    // Très utile pour Book.getRate() → uniformise le nom
    @Transient
    public double getNote() {
        return hasRating() ? rating.doubleValue() : 0.0;
    }
}