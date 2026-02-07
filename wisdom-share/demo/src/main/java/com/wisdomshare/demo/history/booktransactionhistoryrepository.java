package com.wisdomshare.demo.history;

import com.wisdomshare.demo.history.booktransactionhistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface booktransactionhistoryrepository
        extends JpaRepository<booktransactionhistory, Integer> {

    // 1. Est-ce que l'utilisateur a déjà emprunté ce livre (sans retour approuvé)
    @Query("""
            SELECT
                CASE WHEN COUNT(bth) > 0 THEN true ELSE false END
            FROM booktransactionhistory bth
            WHERE bth.user.id = :userId
              AND bth.book.id = :bookId
              AND bth.returnApproved = false
            """)
    boolean isAlreadyBorrowedByUser(
            @Param("bookId") Integer bookId,
            @Param("userId") String userId);

    // 2. Est-ce que le livre est emprunté par quelqu'un (quelconque)
    @Query("""
            SELECT
                CASE WHEN COUNT(bth) > 0 THEN true ELSE false END
            FROM booktransactionhistory bth
            WHERE bth.book.id = :bookId
              AND bth.returnApproved = false
            """)
    boolean isAlreadyBorrowed(@Param("bookId") Integer bookId);

    // 3. Trouver l'emprunt actif d'un utilisateur pour un livre précis
    @Query("""
            SELECT h
            FROM booktransactionhistory h
            WHERE h.user.id = :userId
              AND h.book.id = :bookId
              AND h.returned = false
              AND h.returnApproved = false
            """)
    Optional<booktransactionhistory> findByBookIdAndUserId(
            @Param("bookId") Integer bookId,
            @Param("userId") String userId);

    // 4. Trouver l'emprunt en attente de validation de retour (pour le
    // propriétaire)
    @Query("""
            SELECT h
            FROM booktransactionhistory h
            WHERE h.book.owner.id = :ownerId
              AND h.book.id = :bookId
              AND h.returned = true
              AND h.returnApproved = false
            """)
    Optional<booktransactionhistory> findByBookIdAndOwnerId(
            @Param("bookId") Integer bookId,
            @Param("ownerId") String ownerId);

    // 5. Tous les livres empruntés par un utilisateur (pagination)
    @Query("""
            SELECT h
            FROM booktransactionhistory h
            WHERE h.user.id = :userId
            ORDER BY h.borrowDate DESC
            """)
    Page<booktransactionhistory> findAllBorrowedBooks(
            Pageable pageable,
            @Param("userId") String userId);

    @Query("""
            SELECT h
            FROM booktransactionhistory h
            WHERE h.user.id = :userId
              AND h.returned = false
              AND h.active = true
            ORDER BY h.borrowDate DESC
            """)
    Page<booktransactionhistory> findAllBorrowedByUser(
            @Param("userId") String userId,
            Pageable pageable);

    @Query("""
            SELECT h
            FROM booktransactionhistory h
            WHERE h.user.id = :userId
              AND h.returned = true
            ORDER BY h.actualReturnDate DESC
            """)
    Page<booktransactionhistory> findAllReturnedBooks(
            @Param("userId") String userId,
            Pageable pageable);

}