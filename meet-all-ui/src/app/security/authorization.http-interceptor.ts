import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {SecurityService} from './security.service';

@Injectable()
export class AuthorizationHttpInterceptor implements HttpInterceptor {

    constructor(private securityService: SecurityService) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (this.securityService.isLogged()) {
            req = req.clone({headers: req.headers.set('Authorization', this.securityService.token)});
        }
        return next.handle(req);
    }

}
