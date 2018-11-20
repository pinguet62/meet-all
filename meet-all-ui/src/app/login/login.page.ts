import {Component} from "@angular/core";
import {LoadingController} from "@ionic/angular";
import {LoginService} from "./login.service";

@Component({
    selector: 'app-login',
    template: `
        <ion-content>
            <ion-item>
                <ion-input [(ngModel)]="email" type="text" required email placeholder="Email"></ion-input>
            </ion-item>
            <ion-item>
                <ion-input [(ngModel)]="password" type="password" required placeholder="Password"></ion-input>
            </ion-item>
            <ion-item>
                <ion-button color="primary" (click)="login()">Login</ion-button>
                <ion-button color="secondary" (click)="createAccount()">Create account</ion-button>
            </ion-item>
        </ion-content>
    `,
})
export class LoginPage {

    email: string;
    password: string;

    constructor(private loadingController: LoadingController, private loginService: LoginService) {
    }

    createAccount() {
        this.loginService.createAccount(this.email, this.password).subscribe(
            success => console.log("success", success),
            error => console.error("error", error)
        );
    }

    login() {
        this.loginService.login(this.email, this.password).subscribe(
            success => console.log("success", success),
            error => console.error("error", error)
        );
    }

}
