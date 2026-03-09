import { Component, OnInit } from '@angular/core';
import {PageResponseBookResponse} from '../../../../services/models/page-response-book-response';
import {BookResponse} from '../../../../services/models/book-response';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-borrowed-book-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './borrowed-book-list.component.html',
  styleUrls: ['./borrowed-book-list.component.scss']
})
export class BorrowedBookListComponent implements OnInit {

  borrowedBooks: PageResponseBorrowedBookResponse = {};
  selectedBook: BorrowedBookResponse | undefined = undefined;
  page = 0;
  size = 5;
  pages: any = [];

  constructor(
    private bookService: BookService
  ) { }

  ngOnInit(): void {
    this.findAllBorrowedBooks();
  }

  private findAllBorrowedBooks() {
    this.bookService.findAllBorrowedBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (resp) => {
        this.borrowedBooks = resp;
        this.pages = Array(this.borrowedBooks.totalPages)
          .fill(0)
          .map((x, i) => i);
      }
    });
  }

  returnBorrowedBook(book: BorrowedBookResponse) {
    this.selectedBook = book;
  }

  // Cette méthode est appelée quand l'utilisateur confirme le retour
  returnBook(withFeedback: boolean) {
    this.bookService.returnBorrowBook({
      'book-id': this.selectedBook?.id as number
    }).subscribe({
      next: () => {
        if (withFeedback) {
          // Logique pour envoyer un feedback si nécessaire (optionnel dans la vidéo)
        }
        this.selectedBook = undefined;
        this.findAllBorrowedBooks();
      }
    });
  }

  // Méthodes de pagination
  goToFirstPage() { this.page = 0; this.findAllBorrowedBooks(); }
  goToPreviousPage() { if (this.page > 0) { this.page--; this.findAllBorrowedBooks(); } }
  goToPage(pageIndex: number) { this.page = pageIndex; this.findAllBorrowedBooks(); }
  goToNextPage() { if (this.page < (this.borrowedBooks.totalPages as number) - 1) { this.page++; this.findAllBorrowedBooks(); } }
  goToLastPage() { this.page = (this.borrowedBooks.totalPages as number) - 1; this.findAllBorrowedBooks(); }

  get isLastPage() {
    return this.page === (this.borrowedBooks.totalPages as number) - 1;
  }
}