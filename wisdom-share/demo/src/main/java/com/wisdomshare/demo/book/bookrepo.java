package com.wisdomshare.demo.book;



import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface bookrepo extends JpaRepository<book, Integer> {

    Optional<book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);
}