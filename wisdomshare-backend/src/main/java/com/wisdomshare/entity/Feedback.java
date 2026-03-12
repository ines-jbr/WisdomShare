package com.wisdomshare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
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
@Table(name = "feedback")
@EntityListeners(AuditingEntityListener.class)
public class Feedback {

    @Id
    @GeneratedValue
    private Integer id;

    private Double note;
    private String comment;

    // Store Keycloak user ID as a plain string instead of @ManyToOne User,
    // since User is not a JPA entity (managed by Keycloak externally)
    @CreatedBy
    @Column(name = "user_id", updatable = false)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;
}