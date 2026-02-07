package com.wisdomshare.demo.book;

import com.wisdomshare.demo.history.booktransactionhistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface bookmapper {

    // ───────────────────────────────────────────────
    // Création d'un Book à partir de BookRequest
    // ───────────────────────────────────────────────
    @Mapping(target = "id", ignore = true) // généré par la base
    @Mapping(target = "owner", ignore = true) // setté manuellement dans le service
    @Mapping(target = "archived", constant = "false")
    @Mapping(target = "shareable", source = "shareable", defaultValue = "true")
    @Mapping(target = "coverImageUrl", source = "coverImageUrl") // ou bookCover selon ton choix
    @Mapping(target = "feedbacks", ignore = true)
    @Mapping(target = "histories", ignore = true)
    book toBook(bookrequest request);

    // ───────────────────────────────────────────────
    // Conversion Book → BookResponse (DTO public)
    // ───────────────────────────────────────────────
    @Mapping(target = "rate", qualifiedByName = "calculateRate")
    @Mapping(target = "cover", expression = "java(FileUtils.readFileFromLocation(book.getBookCover()))")
    @Mapping(target = "ownerFullName", expression = "java(book.getOwner() != null ? book.getOwner().fullName() : null)")
    @Mapping(target = "ownerId", source = "owner.id")
    bookresponse toBookResponse(book book);

    // ───────────────────────────────────────────────
    // Mise à jour d'un Book existant à partir de BookRequest
    // ───────────────────────────────────────────────
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "feedbacks", ignore = true)
    @Mapping(target = "histories", ignore = true)
    @Mapping(target = "archived", ignore = true) // modifié via endpoint dédié
    @Mapping(target = "shareable", ignore = true) // modifié via endpoint dédié
    void updateBookFromRequest(bookrequest request, @MappingTarget book book);

    // ───────────────────────────────────────────────
    // Conversion historique emprunt → BorrowedBookResponse
    // ───────────────────────────────────────────────
    @Mapping(target = "id", source = "book.id")
    @Mapping(target = "title", source = "book.title")
    @Mapping(target = "authorName", source = "book.authorName")
    @Mapping(target = "isbn", source = "book.isbn")
    @Mapping(target = "rate", source = "book.rate", qualifiedByName = "calculateRate")
    @Mapping(target = "returned", source = "returned")
    @Mapping(target = "returnApproved", source = "returnApproved")
    borrowedbookresponse toBorrowedBookResponse(booktransactionhistory history);

    // ───────────────────────────────────────────────
    // Méthode nommée pour calculer la note moyenne (réutilisable)
    // ───────────────────────────────────────────────
    @Named("calculateRate")
    default double calculateRate(book book) {
        return book.getRate(); // utilise la méthode @Transient getRate() de l'entité
    }
}