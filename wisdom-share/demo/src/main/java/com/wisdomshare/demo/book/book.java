package com.wisdomshare.demo.book;

import com.wisdomshare.demo.common.baseentity;
import com.wisdomshare.demo.feedback.feedback;
import com.wisdomshare.demo.history.booktransactionhistory;
import com.wisdomshare.demo.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class book extends baseentity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "author_name", nullable = false, length = 150)
    private String authorName;

    @Column(unique = true, nullable = false, length = 20)
    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Column(name = "book_cover", length = 500)
    private String bookCover; // ou coverImageUrl selon ta préférence

    @Column(nullable = false)
    private boolean archived = false;

    @Column(nullable = false)
    private boolean shareable = true;

    // Propriétaire du livre (celui qui l'a ajouté)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "createdby", nullable = false, updatable = false)
    private String createdBy;

    // Avis / notes laissés sur ce livre
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<feedback> feedbacks;

    // Historique des emprunts / transactions
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<booktransactionhistory> histories;

    /**
     * Note moyenne du livre (arrondie à une décimale)
     */
    @Transient
    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }

        double average = feedbacks.stream()
                .mapToDouble(feedback::getNote)
                .average()
                .orElse(0.0);

        return Math.round(average * 10.0) / 10.0;
    }
}