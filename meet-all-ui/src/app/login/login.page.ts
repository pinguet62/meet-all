import {Component} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {AlertController, LoadingController} from "@ionic/angular";
import {tap} from "rxjs/operators";
import {catchErrorAndPresentAlert} from "../alert-controller.utils";
import {processLoading} from "../loading-controller.utils";
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

    originalUrl: string;

    email: string;
    password: string;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private loadingController: LoadingController,
        private alertController: AlertController,
        private loginService: LoginService,
    ) {
        this.originalUrl = this.route.snapshot.queryParams['redirect_url'] || '/';
    }

    createAccount() {
        processLoading(this.loadingController, this.loginService.createAccount(this.email, this.password))
            .pipe(tap(() => this.router.navigate([this.originalUrl])))
            .pipe(catchErrorAndPresentAlert(this.alertController, {
                header: 'Error',
                subHeader: 'Create account',
                message: "Error creating account",
                buttons: ['OK'],
            }))
            .subscribe();
    }

    login() {
        processLoading(this.loadingController, this.loginService.login(this.email, this.password))
            .pipe(tap(() => this.router.navigate([this.originalUrl])))
            .pipe(catchErrorAndPresentAlert(this.alertController, {
                header: 'Error',
                subHeader: 'Login',
                message: "Invalid username or password",
                buttons: ['OK'],
            }))
            .subscribe();
    }

}
