import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {RouteReuseStrategy} from '@angular/router';
import {ServiceWorkerModule} from '@angular/service-worker';
import {IonicModule, IonicRouteStrategy} from '@ionic/angular';
import {environment} from '../environments/environment';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {CredentialModule} from './credentials/credential.module';
import {I18nModule} from './i18n';
import {SecurityModule} from './security';
import {VersionComponent} from './version/version.component';

@NgModule({
    bootstrap: [AppComponent],
    imports: [
        // libs
        BrowserModule,
        IonicModule.forRoot(),
        // app
        ServiceWorkerModule.register('ngsw-worker.js', {enabled: environment.production}),
        AppRoutingModule,
        CredentialModule.forRoot(),
        I18nModule,
        SecurityModule.forRoot(),
    ],
    providers: [
        // libs
        {provide: RouteReuseStrategy, useClass: IonicRouteStrategy},
        // app
    ],
    declarations: [
        AppComponent,
        VersionComponent,
    ],
})
export class AppModule {
}
