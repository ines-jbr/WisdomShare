import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import {MenuComponent} from '../../component/menu/menu.component';


@Component({
  selector: 'app-main',
  standalone: true,
  imports: [RouterModule, MenuComponent],
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent { }
