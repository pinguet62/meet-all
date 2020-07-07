import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {getLanguagesWeighted} from './utils';

@Injectable()
export class AcceptLanguageHttpInterceptor implements HttpInterceptor {

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        req = req.clone({headers: req.headers.set('Accept-Language', getLanguagesWeighted())});
        return next.handle(req);
    }

}
