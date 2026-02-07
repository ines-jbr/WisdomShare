package com.wisdomshare.demo.book;

import com.wisdomshare.demo.common.pageresponse;
import com.wisdomshare.demo.exception.operationnotpermittedexception;
import com.wisdomshare.demo.handler.BusinessException;
import com.wisdomshare.demo.user.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Objects;
import com.wisdomshare.demo.history.booktransactionhistory;
import com.wisdomshare.demo.book.bookrepo;
import com.wisdomshare.demo.history.booktransactionhistoryrepository;
import com.wisdomshare.demo.auth.AuthenticationController;
import org.springframework.security.core.Authentication;

@Service
@RequiredArgsConstructor
@Transactional
public class bookservice {

    private final bookrepo bookRepository;
    private final bookmapper bookMapper;
    private final booktransactionhistoryrepository transactionHistoryRepository;

    public Integer save(bookrequest request, User connectedUser) {
        // Check if ISBN already exists (for create)
        if (request.id() == null && bookRepository.existsByIsbn(request.isbn())) {
            throw new BusinessException("ISBN already exists");
        }

        book book;
        if (request.id() != null) {
            // Update
            book = bookRepository.findById(request.id())
                    .orElseThrow(() -> new BusinessException("Book not found"));
            bookMapper.updateBookFromRequest(request, book);
        } else {
            // Create
            book = bookMapper.toBook(request);
        }

        // Set owner only on create
        if (book.getOwner() == null) {
            book.setOwner(connectedUser);
        }

        book = bookRepository.save(book);
        return book.getId();
    }

    public bookresponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new BusinessException("Book not found"));
    }

    public pageresponse<bookresponse> findAllBooks(int page, int size, User connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        // Most common choice: show shareable + not archived + not own books
        Page<book> books = bookRepository.findAllDisplayableBooks(pageable, connectedUser.getName()); // or
                                                                                                      // .getId().toString()

        return toPageResponse(books);
    }

    public pageresponse<bookresponse> findAllBooksByOwner(int page, int size, User connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<book> books = bookRepository.findAll(bookspecification.withOwnerId(connectedUser.getName()), pageable);

        return toPageResponse(books);
    }

    public Integer updateShareableStatus(Integer bookId, User connectedUser) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException("Book not found with id: " + bookId));

        if (!Objects.equals(book.getCreatedBy(), connectedUser.getName())) {
            throw new operationnotpermittedexception("You cannot update shareable status of someone else's book");
        }

        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    // ───────────────────────────────────────────────
    // Changer l'état "archivé" d'un livre
    // ───────────────────────────────────────────────
    public Integer updateArchivedStatus(Integer bookId, User connectedUser) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException("Book not found with id: " + bookId));

        if (!Objects.equals(book.getCreatedBy(), connectedUser.getName())) {
            throw new operationnotpermittedexception("You cannot update archived status of someone else's book");
        }

        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    // ───────────────────────────────────────────────
    // Emprunter un livre
    // ───────────────────────────────────────────────
    public Integer borrowBook(Integer bookId, User connectedUser) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException("Book not found with id: " + bookId));

        if (book.isArchived() || !book.isShareable()) {
            throw new BusinessException("This book cannot be borrowed (archived or not shareable)");
        }

        if (Objects.equals(book.getCreatedBy(), connectedUser.getName())) {
            throw new BusinessException("You cannot borrow your own book");
        }

        // Déjà emprunté par cet utilisateur ?
        boolean alreadyBorrowedByThisUser = transactionHistoryRepository.isAlreadyBorrowedByUser(
                bookId, connectedUser.getName());
        if (alreadyBorrowedByThisUser) {
            throw new BusinessException("You have already borrowed this book and return is not yet approved");
        }

        // Déjà emprunté par quelqu'un d'autre ?
        boolean alreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowed(bookId);
        if (alreadyBorrowed) {
            throw new BusinessException("This book is already borrowed by someone else");
        }

        booktransactionhistory history = booktransactionhistory.builder()
                .user(connectedUser)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();

        return transactionHistoryRepository.save(history).getId();
    }

    public pageresponse<borrowedbookresponse> findAllBorrowedBooks(int page, int size, User connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());

        Page<booktransactionhistory> histories = transactionHistoryRepository.findAllBorrowedByUser(
                connectedUser.getName(),
                pageable);

        List<borrowedbookresponse> dtos = histories.getContent().stream()
                .map(history -> {
                    borrowedbookresponse dto = new borrowedbookresponse();
                    dto.setId(history.getBook().getId());
                    dto.setTitle(history.getBook().getTitle());
                    dto.setAuthorName(history.getBook().getAuthorName());
                    dto.setIsbn(history.getBook().getIsbn());
                    dto.setRate(history.getBook().getRate());
                    dto.setReturned(history.isReturned());
                    dto.setReturnApproved(history.isReturnApproved());
                    return dto;
                })
                .toList();

        return new pageresponse<>(
                dtos,
                histories.getNumber(),
                histories.getSize(),
                histories.getTotalElements(),
                histories.getTotalPages(),
                histories.isFirst(),
                histories.isLast());

    }

    public pageresponse<borrowedbookresponse> findAllReturnedBooks(int page, int size, User connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("actualReturnDate").descending());

        Page<booktransactionhistory> histories = transactionHistoryRepository.findAllReturnedBooks(
                connectedUser.getName(),
                pageable);

        List<borrowedbookresponse> dtos = histories.getContent().stream()
                .map(history -> {
                    borrowedbookresponse dto = new borrowedbookresponse();
                    dto.setId(history.getBook().getId());
                    dto.setTitle(history.getBook().getTitle());
                    dto.setAuthorName(history.getBook().getAuthorName());
                    dto.setIsbn(history.getBook().getIsbn());
                    dto.setRate(history.getBook().getRate());
                    dto.setReturned(history.isReturned());
                    dto.setReturnApproved(history.isReturnApproved());
                    return dto;
                })
                .toList();

        return new pageresponse<>(
                dtos,
                histories.getNumber(),
                histories.getSize(),
                histories.getTotalElements(),
                histories.getTotalPages(),
                histories.isFirst(),
                histories.isLast());
    }

    public Integer returnBorrowedBook(Integer bookId, User connectedUser) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException("Book not found with id: " + bookId));

        if (book.isArchived() || !book.isShareable()) {
            throw new BusinessException("The requested book is archived or not shareable");
        }

        if (Objects.equals(book.getCreatedBy(), connectedUser.getName())) {
            throw new BusinessException("You cannot return your own book");
        }

        booktransactionhistory history = transactionHistoryRepository.findByBookIdAndUserId(
                bookId, connectedUser.getName())
                .orElseThrow(() -> new BusinessException("You did not borrow this book"));

        history.setReturned(true);
        transactionHistoryRepository.save(history);

        return bookId; // ou history.getId() si tu veux retourner l'ID de la transaction
    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new BusinessException("The requested book is archived or not shareable");
        }
        // User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getCreatedBy(), connectedUser.getName())) {
            throw new BusinessException("You cannot approve the return of a book you do not own");
        }

        booktransactionhistory bookTransactionHistory = transactionHistoryRepository
                .findByBookIdAndOwnerId(bookId, connectedUser.getName())
                .orElseThrow(
                        () -> new BusinessException("The book is not returned yet. You cannot approve its return"));

        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    private pageresponse<bookresponse> toPageResponse(Page<book> page) {

        List<bookresponse> content = page.getContent().stream()
                .map(bookMapper::toBookResponse)
                .toList();

        return new pageresponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }

}