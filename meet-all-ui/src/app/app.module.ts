import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {RouteReuseStrategy} from '@angular/router';
import {IonicModule, IonicRouteStrategy} from '@ionic/angular';
import {SplashScreen} from '@ionic-native/splash-screen/ngx';
import {StatusBar} from '@ionic-native/status-bar/ngx';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {SecurityModule} from './security';

@NgModule({
    declarations: [AppComponent],
    imports: [
        // libs
        BrowserModule,
        IonicModule.forRoot(),
        // app
        AppRoutingModule,
        SecurityModule.forRoot(),
    ],
    providers: [
        // libs
        StatusBar,
        SplashScreen,
        {provide: RouteReuseStrategy, useClass: IonicRouteStrategy},
        // app
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
