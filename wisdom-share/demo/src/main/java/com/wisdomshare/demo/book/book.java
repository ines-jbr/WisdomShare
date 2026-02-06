package com.wisdomshare.demo.book;

import com.wisdomshare.demo.common.baseentity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import com.wisdomshare.demo.user.User;
import jakarta.persistence.*;

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

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column
    private Boolean shareable = true; // Default to shareable

    // The user who owns/added this book
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}