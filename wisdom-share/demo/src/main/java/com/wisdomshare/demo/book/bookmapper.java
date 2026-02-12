package com.wisdomshare.demo.book;

import com.wisdomshare.demo.file.FileUtils;
import com.wisdomshare.demo.history.booktransactionhistory;
import org.springframework.stereotype.Service;

@Service
public class bookmapper {

    public book toBook(bookrequest request) {
        return book.builder()
                .id(request.id())
                .title(request.title())
                .authorName(request.authorName())
                .isbn(request.isbn())
                .synopsis(request.synopsis())
                .archived(request.archived()) // Maintenant ça marche car on l'a ajouté au request
                .shareable(request.shareable())
                .build();
    }

    public bookresponse toBookResponse(book book) {
        return bookresponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .ownerFullName(book.getOwner().getFullName()) // fullName() -> getFullName()
                .coverImageUrl(book.getBookCover()) // Utilise le nom exact de ton record bookresponse
                .build();
    }

    // ... toBorrowedBookResponse reste identique ...

    public void updateBookFromRequest(bookrequest request, book book) {
        book.setTitle(request.title());
        book.setAuthorName(request.authorName());
        book.setIsbn(request.isbn());
        book.setSynopsis(request.synopsis());
        book.setShareable(request.shareable());
        book.setArchived(request.archived()); // Maintenant ça marche !
    }
}