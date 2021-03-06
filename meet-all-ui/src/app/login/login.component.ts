import {Component} from '@angular/core';
import {LoginService} from './login.service';

@Component({
    selector: 'app-login',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-title i18n="@@login.title">Login</ion-title>
            </ion-toolbar>
        </ion-header>

        <ion-content>
            <ion-item>
                <ion-button (click)="login()" color="facebook" i18n="@@login.facebook.button">
                    <ion-icon slot="start" name="logo-facebook"></ion-icon>
                    Continue with Facebook
                </ion-button>
            </ion-item>
        </ion-content>`,
    styles: [`
        .ion-color-facebook {
            --ion-color-base: #4267B2;
        }
    `],
})
export class LoginComponent {

    login() {
        window.location.href = LoginService.buildFacebookOAuthLoginUrl(window.location);
    }

}
