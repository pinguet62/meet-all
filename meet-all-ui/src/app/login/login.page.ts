import {Component} from '@angular/core';
import {LoginService} from './login.service';

@Component({
    selector: 'app-login',
    template: `
        <ion-content>
            <ion-item>
                <ion-button (click)="login()">Facebook</ion-button>
            </ion-item>
        </ion-content>
    `,
})
export class LoginPage {

    login() {
        window.location.href = LoginService.buildFacebookOAuthLoginUrl(window.location);
    }

}
