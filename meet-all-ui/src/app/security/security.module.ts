import {CommonModule} from '@angular/common';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {ModuleWithProviders, NgModule} from '@angular/core';
import {AuthorizationHttpInterceptor} from './authorization.http-interceptor';
import {SecurityGuard} from './security.guard';
import {SecurityService} from './security.service';
import {TokenHttpInterceptor} from './token.http-interceptor';

@NgModule({
    imports: [
        // libs
        CommonModule,
    ],
    providers: [
        {provide: HTTP_INTERCEPTORS, useClass: TokenHttpInterceptor, multi: true},
        {provide: HTTP_INTERCEPTORS, useClass: AuthorizationHttpInterceptor, multi: true},
        SecurityGuard,
    ],
})
export class SecurityModule {

    static forRoot(): ModuleWithProviders<SecurityModule> {
        return {
            ngModule: SecurityModule,
            providers: [
                SecurityService, // app singleton
            ]
        };
    }

}
