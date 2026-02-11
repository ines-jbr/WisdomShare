package com.wisdomshare.demo.book;

import com.wisdomshare.demo.common.baseentity;
import com.wisdomshare.demo.feedback.feedback;
import com.wisdomshare.demo.history.booktransactionhistory;
import com.wisdomshare.demo.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class book extends baseentity {

    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<booktransactionhistory> histories;

    @Builder
    // LE NOM ICI DOIT ETRE "book" POUR MATCH LA CLASSE
    public book(Integer id, String title, String authorName, String isbn, String synopsis, String bookCover, boolean archived, boolean shareable, User owner) {
        this.setId(id);
        this.title = title;
        this.authorName = authorName;
        this.isbn = isbn;
        this.synopsis = synopsis;
        this.bookCover = bookCover;
        this.archived = archived;
        this.shareable = shareable;
        this.owner = owner;
    }

    @Transient
    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        var rate = feedbacks.stream()
                .mapToDouble(f -> f.getNote())
                .average()
                .orElse(0.0);
        return Math.round(rate * 10.0) / 10.0;
    }
}