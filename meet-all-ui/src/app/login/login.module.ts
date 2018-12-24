import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {RouterModule} from '@angular/router';
import {IonicModule} from '@ionic/angular';
import {LoginPage} from './login.page';
import {LoginService} from './login.service';
import {SecurityModule} from '../security';
import {OAuthInterceptorPage} from './oauth-interceptor.page';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        HttpClientModule,
        FormsModule,
        // app
        RouterModule.forChild([
            {path: '', component: LoginPage},
            {path: 'oauth', component: OAuthInterceptorPage}
        ]),
        SecurityModule,
    ],
    declarations: [
        LoginPage,
        OAuthInterceptorPage,
    ],
    providers: [
        LoginService,
    ],
})
export class LoginModule {
}
