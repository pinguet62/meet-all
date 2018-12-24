import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {SecurityService} from './security.service';

@Injectable()
export class SecurityGuard implements CanActivate {

    constructor(
        private router: Router,
        private securityService: SecurityService
    ) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        if (this.securityService.isLogged()) {
            return true;
        }

        this.router.navigate(['/login']);
        return false;
    }

}
