package com.wisdomshare.demo.book;

import com.wisdomshare.demo.common.pageresponse;
import com.wisdomshare.demo.user.User;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class bookcontroller {

    private final bookservice bookService;

    @PostMapping
    public ResponseEntity<Integer> saveBook(
            @RequestBody @Valid bookrequest request,
            Authentication connectedUser) {
        Integer bookId = bookService.save(request, (User) connectedUser.getPrincipal());
        return ResponseEntity.status(CREATED).body(bookId);
    }

    @GetMapping("/{book-id}")
    public ResponseEntity<bookresponse> findById(@PathVariable("book-id") Integer bookId) {
        return ResponseEntity.ok(bookService.findById(bookId));
    }

    @GetMapping
    public ResponseEntity<pageresponse<bookresponse>> findAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return ResponseEntity.ok(bookService.findAllBooks(page, size, user));
    }

    @GetMapping("/owner")
    public ResponseEntity<pageresponse<bookresponse>> findAllBooksByOwner(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, user));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<pageresponse<borrowedbookresponse>> findAllBorrowedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return ResponseEntity.ok(bookService.findAllBorrowedBooks(page, size, user));
    }

    @GetMapping("/returned")
    public ResponseEntity<pageresponse<borrowedbookresponse>> findAllReturnedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return ResponseEntity.ok(bookService.findAllReturnedBooks(page, size, user));
    }

    // Changer statut shareable
    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return ResponseEntity.ok(bookService.updateShareableStatus(bookId, user));
    }

    // Changer statut archived
    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, user));
    }

    // Emprunter un livre
    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return ResponseEntity.ok(bookService.borrowBook(bookId, user));
    }

    // Retourner un livre emprunté
    @PatchMapping("/borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowedBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return ResponseEntity.ok(bookService.returnBorrowedBook(bookId, user));
    }

    @PatchMapping("/borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> approveReturnBorrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        // Ajout du cast explicite ici
        User user = (User) connectedUser.getPrincipal();
        return ResponseEntity.ok(bookService.approveReturnBorrowedBook(bookId, user));
    }
    
    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookPicture(
            @PathVariable("book-id") Integer bookId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
    ) {
        // On extrait l'utilisateur avant de le passer au service
        User user = (User) connectedUser.getPrincipal(); 
        bookService.uploadBookCoverPicture(file, user, bookId);
        return ResponseEntity.accepted().build();
    }
    
}
