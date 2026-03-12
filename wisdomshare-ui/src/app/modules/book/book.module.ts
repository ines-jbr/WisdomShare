import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BookRoutingModule} from './book-routing.module';
import {MainComponent} from './pages/main/main.component';
import {BookDetailsComponent} from './pages/book-details/book-details.component';
import {RatingComponent} from './component/rating/rating.component';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    BookRoutingModule,
    MainComponent,
    BookDetailsComponent,
    RatingComponent
  ]
})
export class BookModule {}
