import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  set token(token: string) {
    localStorage.setItem('token', token);
  }

  get token(): string {
    return localStorage.getItem('token') as string;
  }

  /**
   * Vérifie si le token est valide. 
   * Si expiré, nettoie automatiquement le localStorage.
   */
  isTokenValid(): boolean {
    const token = this.token;
    if (!token) {
      return false;
    }
    
    const jwtHelper = new JwtHelperService();
    const isTokenExpired = jwtHelper.isTokenExpired(token);
    
    if (isTokenExpired) {
      // RESET : On vide tout pour forcer une nouvelle connexion
      localStorage.clear(); 
      return false;
    }
    return true;
  }

  isTokenNotValid(): boolean {
    return !this.isTokenValid();
  }

  /**
   * Extrait les permissions (authorities) du JWT
   */
  get userRoles(): string[] {
    const token = this.token;
    if (token) {
      const jwtHelper = new JwtHelperService();
      const decodedToken = jwtHelper.decodeToken(token);
      
      // Dans le backend Spring Boot, les rôles sont souvent dans 'authorities'
      return decodedToken.authorities || [];
    }
    return [];
  }
}