import {Router} from '@angular/router';
import {of, throwError} from 'rxjs';
import {TokenHttpInterceptor} from './token.http-interceptor';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpRequest} from '@angular/common/http';
import {allMethodNames} from '../test-utils';
import {SecurityService} from './security.service';

describe('token.http-interceptor', () => {
    let router: jasmine.SpyObj<Router>;
    let securityService: SecurityService;
    let httpInterceptor: TokenHttpInterceptor;
    let req: jasmine.SpyObj<HttpRequest<any>>;
    let next: jasmine.SpyObj<HttpHandler>;
    beforeEach(() => {
        router = jasmine.createSpyObj<Router>(Router.name, allMethodNames(Router));
        securityService = new SecurityService();
        securityService.token = 'token';
        httpInterceptor = new TokenHttpInterceptor(router, securityService);
        req = jasmine.createSpyObj<HttpRequest<any>>(HttpRequest.name, allMethodNames(HttpRequest));
        next = jasmine.createSpyObj<HttpHandler>(HttpHandler.name, ['handle']);
    });

    it('When success: no action', done => {
        const event = jasmine.createSpy('HttpEvent');
        next.handle.and.returnValue(of(event));

        const result = httpInterceptor.intercept(req, next);

        result.subscribe(it => {
            expect(router.navigate).not.toHaveBeenCalled();
            expect(securityService.token).not.toBeNull();
            expect(it).toBe(event);
            done();
        });
    });

    it('When error: clean token + redirect to login page', done => {
        const event = new HttpErrorResponse({status: 403});
        next.handle.and.returnValue(throwError(event));

        const result = httpInterceptor.intercept(req, next);

        result.subscribe(
            () => done.fail(),
            err => {
                expect(router.navigate).toHaveBeenCalled();
                expect(securityService.token).toBeNull();
                expect(err).toBe(event);
                done();
            });
    });
});
