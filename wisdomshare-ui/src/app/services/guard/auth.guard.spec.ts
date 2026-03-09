import {inject} from '@angular/core';
import {CanActivateFn, Router} from '@angular/router';
import {TokenService} from '../token/token.service';

/**
 * Guard fonctionnel pour protéger les routes privées
 */
export const authGuard: CanActivateFn = () => {
  // 1. Injection des services nécessaires via la fonction inject()
  const tokenService = inject(TokenService);
  const router = inject(Router);

  // 2. Vérification de la validité du jeton
  // On utilise une méthode du TokenService pour savoir si le token existe et n'est pas expiré
  if (tokenService.isTokenNotValid()) {
    
    // 3. Si le token n'est pas valide (absent ou expiré), on redirige vers le login
    router.navigate(['login']);
    return false; // Accès refusé
  }

  // 4. Si le token est valide, on autorise l'accès à la route
  return true;
};