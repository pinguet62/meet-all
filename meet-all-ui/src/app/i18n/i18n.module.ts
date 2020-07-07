import {CommonModule} from '@angular/common';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {AcceptLanguageHttpInterceptor} from './accept-language.http-interceptor';

@NgModule({
    imports: [
        // libs
        CommonModule,
    ],
    providers: [
        {provide: HTTP_INTERCEPTORS, useClass: AcceptLanguageHttpInterceptor, multi: true},
    ],
})
export class I18nModule {
}
