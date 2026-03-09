package com.wisdomshare.demo.book;

import com.wisdomshare.demo.common.pageresponse;
import com.wisdomshare.demo.exception.operationnotpermittedexception;
import com.wisdomshare.demo.user.User;
import com.wisdomshare.demo.handler.BusinessException;
import com.wisdomshare.demo.history.booktransactionhistory;
import com.wisdomshare.demo.history.booktransactionhistoryrepository;
import com.wisdomshare.demo.file.fileStorageService; // Package corrigé généralement utilisé
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class bookservice {

    private final bookrepo bookRepository;
    private final bookmapper bookMapper;
    private final booktransactionhistoryrepository transactionHistoryRepository;
    private final fileStorageService fileStorageServices;

    public Integer save(bookrequest request, User connectedUser) {
        if (request.id() == null && bookRepository.existsByIsbn(request.isbn())) {
            throw new BusinessException("ISBN already exists");
        }
        book book;
        if (request.id() != null) {
            book = bookRepository.findById(request.id())
                    .orElseThrow(() -> new EntityNotFoundException("Book not found"));
            bookMapper.updateBookFromRequest(request, book);
        } else {
            book = bookMapper.toBook(request);
        }
        book.setOwner(connectedUser);
        return bookRepository.save(book).getId();
    }

    public bookresponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID: " + bookId));
    }

    public pageresponse<bookresponse> findAllBooks(int page, int size, User connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<book> books = bookRepository.findAllDisplayableBooks(pageable, connectedUser.getId().toString());
        return toPageResponse(books);
    }

    public pageresponse<bookresponse> findAllBooksByOwner(int page, int size, User connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<book> books = bookRepository.findAll(bookspecification.withOwnerId(connectedUser.getId().toString()), pageable);
        return toPageResponse(books);
    }

    public Integer updateShareableStatus(Integer bookId, User connectedUser) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found"));
        if (!Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new operationnotpermittedexception("You cannot update books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, User connectedUser) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found"));
        if (!Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new operationnotpermittedexception("You cannot update books archived status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Integer borrowBook(Integer bookId, User connectedUser) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found"));

        if (book.isArchived() || !book.isShareable()) {
            throw new operationnotpermittedexception("The requested book cannot be borrowed since it is archived or not shareable");
        }
        if (Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new operationnotpermittedexception("You cannot borrow your own book");
        }
        final boolean isAlreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, connectedUser.getId().toString());
        if (isAlreadyBorrowed) {
            throw new operationnotpermittedexception("The requested book is already borrowed");
        }
        booktransactionhistory history = booktransactionhistory.builder()
                .user(connectedUser)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return transactionHistoryRepository.save(history).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, User connectedUser) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found"));
        if (book.isArchived() || !book.isShareable()) {
            throw new operationnotpermittedexception("The requested book cannot be borrowed");
        }
        if (Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new operationnotpermittedexception("You cannot borrow or return your own book");
        }
        booktransactionhistory history = transactionHistoryRepository.findByBookIdAndUserId(bookId, connectedUser.getId().toString())
                .orElseThrow(() -> new operationnotpermittedexception("You did not borrow this book"));

        history.setReturned(true);
        return transactionHistoryRepository.save(history).getId();
    }

    public Integer approveReturnBorrowedBook(Integer bookId, User connectedUser) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found"));
        if (!Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new operationnotpermittedexception("You cannot approve the return of a book you do not own");
        }
        booktransactionhistory history = transactionHistoryRepository.findByBookIdAndOwnerId(bookId, connectedUser.getId().toString())
                .orElseThrow(() -> new operationnotpermittedexception("The book is not returned yet. You cannot approve its return"));

        history.setReturnApproved(true);
        return transactionHistoryRepository.save(history).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, User connectedUser, Integer bookId) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found"));
        var bookCover = fileStorageServices.saveFile(file, connectedUser.getId(), book.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }

    public pageresponse<borrowedbookresponse> findAllBorrowedBooks(int page, int size, User connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<booktransactionhistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedByUser(connectedUser.getId().toString(), pageable);
        List<borrowedbookresponse> content = allBorrowedBooks.getContent().stream()
                .map(bookMapper::toBorrowedBookResponse) // Utilise le mapper plutôt que du code manuel
                .collect(Collectors.toList());
        return new pageresponse<>(
                content,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public pageresponse<borrowedbookresponse> findAllReturnedBooks(int page, int size, User connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<booktransactionhistory> allBorrowedBooks = transactionHistoryRepository.findAllReturnedBooksByOwner(connectedUser.getId().toString(), pageable);
        List<borrowedbookresponse> content = allBorrowedBooks.getContent().stream()
                .map(bookMapper::toBorrowedBookResponse)
                .collect(Collectors.toList());
        return new pageresponse<>(
                content,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    private pageresponse<bookresponse> toPageResponse(Page<book> page) {
        List<bookresponse> content = page.getContent().stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new pageresponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }
}