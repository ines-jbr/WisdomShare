package com.wisdomshare.demo.book;

import org.springframework.data.jpa.domain.Specification;

public class bookspecification {

    public static Specification<book> withOwnerId(String ownerId) {
        return (root, query, cb) -> cb.equal(root.get("createdBy"), ownerId);
    }

    // Optionnel : si tu veux aussi filtrer par owner (si le champ s'appelle owner
    // au lieu de createdBy)
    // public static Specification<Book> withOwner(User owner) {
    // return (root, query, cb) -> cb.equal(root.get("owner"), owner);
    // }

    // Optionnel : combinaison courante (exemple : non archivé + partageable + pas
    // le propriétaire)
    public static Specification<book> displayableAndNotOwnedBy(String currentUserId) {
        return (root, query, cb) -> cb.and(
                cb.isFalse(root.get("archived")),
                cb.isTrue(root.get("shareable")),
                cb.notEqual(root.get("createdBy"), currentUserId));
    }
}
