package com.wisdomshare.demo.book;

import com.wisdomshare.demo.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
            Authentication connectedUser
    ) {
        Integer bookId = bookService.save(request, (User) connectedUser.getPrincipal());
        return ResponseEntity.status(CREATED).body(bookId);
    }

    @GetMapping("/{book-id}")
    public ResponseEntity<bookresponse> findById(@PathVariable("book-id") Integer bookId) {
        return ResponseEntity.ok(bookService.findById(bookId));
    }
}