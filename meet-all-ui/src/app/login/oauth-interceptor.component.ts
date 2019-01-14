import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {LoadingController} from '@ionic/angular';
import {of} from 'rxjs';
import {finalize, flatMap} from 'rxjs/operators';
import {processLoading} from '../loading-controller.utils';
import {LoginService} from './login.service';

@Component({
    selector: 'app-login-oauth',
    template: `
        <ion-content>
            <ion-item>
                <ion-label>Login in progress...</ion-label>
            </ion-item>
        </ion-content>
    `,
})
export class OauthInterceptorComponent {

    constructor(
        router: Router,
        loadingController: LoadingController,
        loginService: LoginService
    ) {
        processLoading(loadingController,
            of(LoginService.extractOAuthAccessTokenFromUrl(window.location.href))
                .pipe(flatMap(accessToken => loginService.login(accessToken)))
                .pipe(finalize(() => router.navigate(['/']))))
            .subscribe();
    }

}
