import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BookRoutingModule } from './book-routing.module';
import { MainComponent } from './pages/main/main.component';

@NgModule({
  declarations: [
    // Only non-standalone components are declared here
  ],
  imports: [
    CommonModule,
    BookRoutingModule,
    // MainComponent is standalone, so it must be imported here
    MainComponent
  ]
})
export class BookModule { }
