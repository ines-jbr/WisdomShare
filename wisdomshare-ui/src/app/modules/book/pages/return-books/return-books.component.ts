import { Component, OnInit } from '@angular/core';
import { PageresponseBorrowedbookresponse } from '../../../../services/models/pageresponse-borrowedbookresponse';
import { Borrowedbookresponse } from '../../../../services/models/borrowedbookresponse';
import { BookService } from '../../../../services/services/book.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-returned-books',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './return-books.component.html',
  styleUrls: ['./return-books.component.scss']
})
export class ReturnedBooksComponent implements OnInit {

  returnedBooks: PageresponseBorrowedbookresponse = {};
  page = 0;
  size = 5;
  pages: any = [];
  message = '';
  level = 'success';

  constructor(private bookService: BookService) { }

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
          .map((_, i) => i);
      }
    });
  }

  approveBookReturn(book: Borrowedbookresponse) {
    if (!book.returned) {
      this.level = 'error';
      this.message = 'The book is not yet returned';
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

  goToFirstPage() { this.page = 0; this.findAllReturnedBooks(); }
  goToPreviousPage() { if (this.page > 0) { this.page--; this.findAllReturnedBooks(); } }
  goToPage(pageIndex: number) { this.page = pageIndex; this.findAllReturnedBooks(); }
  goToNextPage() { if (this.page < (this.returnedBooks.totalPages as number) - 1) { this.page++; this.findAllReturnedBooks(); } }
  goToLastPage() { this.page = (this.returnedBooks.totalPages as number) - 1; this.findAllReturnedBooks(); }

  get isLastPage(): boolean {
    return this.page === (this.returnedBooks.totalPages as number) - 1;
  }
}
