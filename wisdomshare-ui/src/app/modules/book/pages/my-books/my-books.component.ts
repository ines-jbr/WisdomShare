import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { BookService } from '../../../../services/services/book.service';
import { PageResponseBookResponse } from '../../../../services/models/page-response-book-response';
import { BookResponse } from '../../../../services/models/book-response';
import {BookCardComponent} from '../../component/book-card/book-card.component';


@Component({
  selector: 'app-my-books',
  standalone: true,
  imports: [CommonModule, BookCardComponent, RouterModule],
  templateUrl: './my-books.component.html',
  styleUrls: ['./my-books.component.scss']
})
export class MyBooksComponent implements OnInit {

  bookResponse: PageResponseBookResponse = {};
  page = 0;
  size = 5;
  pages: any = [];

  constructor(
    private bookService: BookService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.findAllBooks();
  }

  private findAllBooks() {
    this.bookService.findAllBooksByOwner({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (books) => {
        this.bookResponse = books;
        this.pages = Array(this.bookResponse.totalPages)
          .fill(0)
          .map((x, i) => i);
      }
    });
  }

  archiveBook(book: BookResponse) {
    this.bookService.updateArchivedStatus({
      'book-id': book.id as number
    }).subscribe({
      next: () => {
        book.archived = !book.archived;
      }
    });
  }

  shareBook(book: BookResponse) {
    this.bookService.updateShareableStatus({
      'book-id': book.id as number
    }).subscribe({
      next: () => {
        book.shareable = !book.shareable;
      }
    });
  }

  editBook(book: BookResponse) {
    this.router.navigate(['books', 'manage', book.id]);
  }

  goToFirstPage() { this.page = 0; this.findAllBooks(); }
  goToPreviousPage() { if (this.page > 0) { this.page--; this.findAllBooks(); } }
  goToPage(pageIndex: number) { this.page = pageIndex; this.findAllBooks(); }
  goToNextPage() { if (this.page < (this.bookResponse.totalPages as number) - 1) { this.page++; this.findAllBooks(); } }
  goToLastPage() { this.page = (this.bookResponse.totalPages as number) - 1; this.findAllBooks(); }

  get isLastPage(): boolean {
    return this.page === (this.bookResponse.totalPages as number) - 1;
  }
}
