import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {SecurityService} from './security.service';

/** Redirect to login when HTTP response code {@code 401}. */
@Injectable()
export class TokenHttpInterceptor implements HttpInterceptor {

    constructor(private router: Router, private securityService: SecurityService) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(req).pipe(
            catchError(evt => {
                if (evt instanceof HttpErrorResponse && [401, 403].includes(evt.status)) {
                    this.securityService.token = null;
                    this.router.navigate(['/login']);
                }
                return throwError(evt);
            })
        );
    }

}
