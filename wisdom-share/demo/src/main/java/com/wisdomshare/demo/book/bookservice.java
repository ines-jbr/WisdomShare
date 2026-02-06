package com.wisdomshare.demo.book;




import com.wisdomshare.demo.handler.BusinessException;
import com.wisdomshare.demo.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class bookservice {

    private final bookrepo bookRepository;
    private final bookmapper bookMapper;

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
}