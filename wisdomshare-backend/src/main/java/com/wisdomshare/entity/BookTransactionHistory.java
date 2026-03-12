package com.wisdomshare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book_transaction_history")
@EntityListeners(AuditingEntityListener.class)
public class BookTransactionHistory {

    @Id
    @GeneratedValue
    private Integer id;

    // Keycloak user ID stored as string (no FK — Keycloak users are external)
    @Column(name = "user_id")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private boolean returned;
    private boolean returnApproved;

    @Enumerated(EnumType.STRING)
    private BookTransactionType type;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;
}