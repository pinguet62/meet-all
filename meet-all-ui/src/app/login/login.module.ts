import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {RouterModule} from '@angular/router';
import {IonicModule} from '@ionic/angular';
import {LoginComponent} from './login.component';
import {LoginService} from './login.service';
import {SecurityModule} from '../security';
import {OauthInterceptorComponent} from './oauth-interceptor.component';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        HttpClientModule,
        FormsModule,
        // app
        RouterModule.forChild([
            {path: '', component: LoginComponent},
            {path: 'oauth', component: OauthInterceptorComponent}
        ]),
        SecurityModule,
    ],
    declarations: [
        LoginComponent,
        OauthInterceptorComponent,
    ],
    providers: [
        LoginService,
    ],
})
export class LoginModule {
}
