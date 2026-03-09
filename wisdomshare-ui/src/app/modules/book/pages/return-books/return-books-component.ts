import { Component, OnInit } from '@angular/core';
import { PageResponseBorrowedBookResponse } from '../../../../services/models/page-response-borrowed-book-response';
import { BorrowedBookResponse } from '../../../../services/models/borrowed-book-response';
import { BookService } from '../../../../services/services/book.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-returned-books',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './returned-books.component.html'
})
export class ReturnedBooksComponent implements OnInit {

  returnedBooks: PageResponseBorrowedBookResponse = {};
  page = 0;
  size = 5;
  pages: any = [];
  message = '';
  level = 'success';

  constructor(
    private bookService: BookService
  ) { }

  ngOnInit(): void {
    this.findAllReturnedBooks();
  }

  private findAllReturnedBooks() {
    this.bookService.findAllReturnedBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (resp) => {
        this.returnedBooks = resp;
        this.pages = Array(this.returnedBooks.totalPages)
          .fill(0)
          .map((x, i) => i);
      }
    });
  }

  // Approuver le retour (utilisé par le propriétaire du livre)
  approveBookReturn(book: BorrowedBookResponse) {
    if (!book.returned) { 
      this.level = 'error';
      this.message ='the book is not yet returned';
      return;
    }
    this.bookService.approveReturnBorrowBook({
      'book-id': book.id as number
    }).subscribe({
      next: () => {
        this.level = 'success';
        this.message = 'Book return approved';
        this.findAllReturnedBooks();
      }
    });
  }

  // --- Méthodes de Pagination ---
  goToFirstPage() { this.page = 0; this.findAllReturnedBooks(); }
  goToPreviousPage() { if (this.page > 0) { this.page--; this.findAllReturnedBooks(); } }
  goToPage(pageIndex: number) { this.page = pageIndex; this.findAllReturnedBooks(); }
  goToNextPage() { if (this.page < (this.returnedBooks.totalPages as number) - 1) { this.page++; this.findAllReturnedBooks(); } }
  goToLastPage() { this.page = (this.returnedBooks.totalPages as number) - 1; this.findAllReturnedBooks(); }

  get isLastPage() {
    return this.page === (this.returnedBooks.totalPages as number) - 1;
  }
}