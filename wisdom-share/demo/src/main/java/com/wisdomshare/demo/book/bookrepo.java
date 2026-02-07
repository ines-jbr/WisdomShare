package com.wisdomshare.demo.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface bookrepo extends JpaRepository<book, Integer>, JpaSpecificationExecutor<book> {

  boolean existsByIsbn(String isbn);

  // Optional<Book> findByIsbn(String isbn); // ← you can add if needed

  @Query("""
      SELECT b
      FROM Book b
      WHERE b.archived = false
        AND b.shareable = true
        AND b.createdBy != :userId
      """)
  Page<book> findAllDisplayableBooks(Pageable pageable, String userId);

}