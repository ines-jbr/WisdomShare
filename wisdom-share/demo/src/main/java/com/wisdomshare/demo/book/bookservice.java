package com.wisdomshare.demo.book;

import com.wisdomshare.demo.common.pageresponse;
import com.wisdomshare.demo.exception.operationnotpermittedexception;
import com.wisdomshare.demo.user.User;
import com.wisdomshare.demo.handler.BusinessException;
import com.wisdomshare.demo.history.booktransactionhistory;
import com.wisdomshare.demo.history.booktransactionhistoryrepository;
import com.wisdomshare.demo.book.fileStorageService; // Vérifie ce package
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;


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
                    .orElseThrow(() -> new BusinessException("Book not found"));
            bookMapper.updateBookFromRequest(request, book);
        } else {

            book = bookMapper.toBook(request);

        }


        if (book.getOwner() == null) {
            book.setOwner(connectedUser);
        }

        return bookRepository.save(book).getId();
    }

    public bookresponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new BusinessException("Book not found"));
    }

    public pageresponse<bookresponse> findAllBooks(int page, int size, User connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<book> books = bookRepository.findAllDisplayableBooks(pageable, connectedUser.getId().toString());
        return toPageResponse(books);
    }

    public pageresponse<bookresponse> findAllBooksByOwner(int page, int size, User connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        // .toString() ajouté ici pour transformer l'Integer en String
        Page<book> books = bookRepository.findAll(
                bookspecification.withOwnerId(connectedUser.getId().toString()), 
                pageable
        );
        return toPageResponse(books);
    }

    public Integer updateShareableStatus(Integer bookId, User connectedUser) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException("Book not found"));
        if (!Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new operationnotpermittedexception("You cannot update someone else's book");
        }

        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }


    public Integer updateArchivedStatus(Integer bookId, User connectedUser) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException("Book not found"));
        if (!Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new operationnotpermittedexception("You cannot update someone else's book");
        }

        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }


    public Integer borrowBook(Integer bookId, User connectedUser) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException("Book not found"));

        if (book.isArchived() || !book.isShareable()) {
            throw new BusinessException("Book cannot be borrowed");
        }
        if (Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new BusinessException("You cannot borrow your own book");
        }


        boolean alreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowed(bookId);
        if (alreadyBorrowed) {
            throw new BusinessException("Book is already borrowed");
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
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<booktransactionhistory> histories = transactionHistoryRepository.findAllBorrowedByUser(connectedUser.getId().toString(), pageable);
        
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
                }).toList();

        return new pageresponse<>(dtos, histories.getNumber(), histories.getSize(), histories.getTotalElements(), histories.getTotalPages(), histories.isFirst(), histories.isLast());
    }

    public pageresponse<borrowedbookresponse> findAllReturnedBooks(int page, int size, User connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<booktransactionhistory> histories = transactionHistoryRepository.findAllReturnedBooks(connectedUser.getId().toString(), pageable);
        
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
                }).toList();

        return new pageresponse<>(dtos, histories.getNumber(), histories.getSize(), histories.getTotalElements(), histories.getTotalPages(), histories.isFirst(), histories.isLast());
    }

    public Integer returnBorrowedBook(Integer bookId, User connectedUser) {
        booktransactionhistory history = transactionHistoryRepository.findByBookIdAndUserId(bookId, connectedUser.getId().toString())
                .orElseThrow(() -> new BusinessException("You did not borrow this book"));

        history.setReturned(true);
        transactionHistoryRepository.save(history);
        return bookId;
    }

    public Integer approveReturnBorrowedBook(Integer bookId, User connectedUser) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found"));
        
        if (!Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new BusinessException("You do not own this book");
        }

        booktransactionhistory bookTransactionHistory = transactionHistoryRepository
                .findByBookIdAndOwnerId(bookId, connectedUser.getId().toString())
                .orElseThrow(() -> new BusinessException("Return not ready for approval"));

        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }


    public void uploadBookCoverPicture(MultipartFile file, User connectedUser, Integer bookId) {
        book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found"));
        if (!Objects.equals(book.getOwner().getId(), connectedUser.getId())) {
            throw new operationnotpermittedexception("You do not own this book");
        }
        var bookCover = fileStorageServices.saveFile(file, connectedUser.getId(), book.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }

    private pageresponse<bookresponse> toPageResponse(Page<book> page) {
        List<bookresponse> content = page.getContent().stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new pageresponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }
}