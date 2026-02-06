package com.wisdomshare.demo.book;



import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface bookmapper {

    @Mapping(target = "owner", ignore = true) // Set manually in service
    book toBook(bookrequest request);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerFullName", expression = "java(book.getOwner() != null ? book.getOwner().fullName() : null)")
    bookresponse toBookResponse(book book);

    // For update
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    void updateBookFromRequest(bookrequest request, @MappingTarget book book);
}