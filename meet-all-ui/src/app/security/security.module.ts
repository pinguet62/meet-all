import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {ModuleWithProviders, NgModule} from '@angular/core';
import {SecurityGuard} from './security.guard';
import {SecurityService} from './security.service';
import {AuthorizationHttpInterceptor} from './authorization.http-interceptor';
import {CommonModule} from '@angular/common';

@NgModule({
    imports: [
        // libs
        CommonModule,
    ],
    providers: [
        {provide: HTTP_INTERCEPTORS, useClass: AuthorizationHttpInterceptor, multi: true},
        SecurityGuard,
    ],
})
export class SecurityModule {

    static forRoot(): ModuleWithProviders {
        return {
            ngModule: SecurityModule,
            providers: [
                SecurityService, // app singleton
            ]
        };
    }

}
