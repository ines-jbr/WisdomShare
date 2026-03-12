import {Component, OnInit} from '@angular/core';
import {AuthenticationRequest} from '../../services/models/authentication-request';

import {Router} from 'express';
import {TokenService} from '../../services/token/token.service';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  imports: [
    RouterOutlet
  ],
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  get in() {
    return this.#in;
  }

  set in(value) {
    this.#in = value;
  }

  authRequest: AuthenticationRequest = {email: '', password: ''};
  errorMsg: Array<string> = [];

  constructor(
    private keyclockService : keyclockService
  ) {
  }

  async ngOnInit(): Promise<void> {
    await this.keyclockService.init();
    await this.keyclockService.login();
  }
/*
  login() {
    this.errorMsg = [];
    this.authService.authenticate({
      body: this.authRequest
    }).subscribe({
      next: (res) => {
        this.tokenService.token = res.token as string;
        this.router.navigate(['books']);
      },
      error: (err) => {
        console.log(err);
        if (err.error.validationErrors) {
          this.errorMsg = err.error.validationErrors;
        } else {
          this.errorMsg.push(err.error.errorMsg);
        }
      }
    });
  }

  register() {
    this.router.navigate(['register']);
  }
}*/
//in the video he ignore it //
