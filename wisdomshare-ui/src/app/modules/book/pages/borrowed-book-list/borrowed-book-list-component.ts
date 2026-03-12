import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookService } from '../../../../services/services/book.service';
import { PageresponseBorrowedbookresponse } from '../../../../services/models/pageresponse-borrowedbookresponse';
import { Borrowedbookresponse } from '../../../../services/models/borrowedbookresponse';
import { FeedbackRequest } from '../../../../services/models/feedback-request';

@Component({
  selector: 'app-borrowed-book-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './borrowed-book-list.component.html',
  styleUrls: ['./borrowed-book-list.component.scss']
})
export class BorrowedBookListComponent implements OnInit {

  borrowedBooks: PageresponseBorrowedbookresponse = {};
  selectedBook: Borrowedbookresponse | undefined = undefined;
  feedbackRequest: FeedbackRequest = { note: 0, comment: '', bookId: 0 };
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

  returnBorrowedBook(book: Borrowedbookresponse) {
    this.selectedBook = book;
    this.feedbackRequest = { note: 0, comment: '', bookId: book.id as number };
  }

  returnBook(withFeedback: boolean) {
    this.bookService.returnBorrowBook({
      'book-id': this.selectedBook?.id as number
    }).subscribe({
      next: () => {
        if (withFeedback && this.feedbackRequest.note) {
          // Feedback logic handled separately if needed
        }
        this.selectedBook = undefined;
        this.findAllBorrowedBooks();
      }
    });
  }

  goToFirstPage() { this.page = 0; this.findAllBorrowedBooks(); }
  goToPreviousPage() { if (this.page > 0) { this.page--; this.findAllBorrowedBooks(); } }
  goToPage(pageIndex: number) { this.page = pageIndex; this.findAllBorrowedBooks(); }
  goToNextPage() { if (this.page < (this.borrowedBooks.totalPages as number) - 1) { this.page++; this.findAllBorrowedBooks(); } }
  goToLastPage() { this.page = (this.borrowedBooks.totalPages as number) - 1; this.findAllBorrowedBooks(); }

  get isLastPage(): boolean {
    return this.page === (this.borrowedBooks.totalPages as number) - 1;
  }
}
