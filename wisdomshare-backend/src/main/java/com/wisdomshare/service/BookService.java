package com.wisdomshare.service;

import com.wisdomshare.entity.Book;
import com.wisdomshare.entity.User;
import com.wisdomshare.model.BookRequest;
import com.wisdomshare.model.BookResponse;
import com.wisdomshare.model.PageResponseBookResponse;
import com.wisdomshare.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Integer save(BookRequest request, Authentication connectedUser) {
        //User user = ((User) connectedUser.getPrincipal());
        Book book = Book.builder()
                .title(request.title())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .shareable(request.shareable())
                .archived(false)
                //*//
                .build();
        return bookRepository.save(book).getId();
    }

    public PageResponseBookResponse findAllBooks(int page, int size, Authentication connectedUser) {
        //User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, Authentication.getName());
        List<BookResponse> bookResponses = books.stream()
                .map(book -> new BookResponse(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthorName(),
                        book.getSynopsis(),
                        book.getBookCover(),
                        book.isArchived(),
                        book.isShareable(),
                        book.getCreatedBy().getFullName(),
                        book.getRate(),
                        false, // rated, simplified
                        List.of() // feedbacks, simplified
                ))
                .toList();
        return new PageResponseBookResponse(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }
}