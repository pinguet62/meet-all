import {HttpErrorResponse} from '@angular/common/http';
import {Component} from '@angular/core';
import {LoadingController, NavController, ToastController} from '@ionic/angular';
import {from, throwError} from 'rxjs';
import {catchError, flatMap, map, tap} from 'rxjs/operators';
import {processLoading} from '../../loading-controller.utils';
import {CredentialService, Provider} from '../credential.service';
import {ApiError, noOp} from '../../utils';

@Component({
    selector: 'app-credential-create',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-buttons slot="start">
                    <ion-back-button></ion-back-button>
                </ion-buttons>
                <ion-title i18n="@@credentials.title">Credentials</ion-title>
                <ion-buttons slot="primary">
                    <ion-button (click)="onCreate()" [disabled]="!form.valid" i18n="@@credentials.create.button">
                        Create
                        <ion-icon name="checkmark"></ion-icon>
                    </ion-button>
                </ion-buttons>
            </ion-toolbar>
        </ion-header>

        <ion-content>
            <form #form="ngForm">
                <ion-item>
                    <ion-label i18n="@@common.model.provider">Provider</ion-label>
                    <ion-select [(ngModel)]="provider" name="provider" required placeholder="Select one">
                        <ion-select-option *ngFor="let provider of providers" [value]="provider">{{provider}}</ion-select-option>
                    </ion-select>
                </ion-item>

                <ion-item>
                    <ion-label i18n="@@credentials.model.email">Facebook email</ion-label>
                    <ion-input type="text" [(ngModel)]="facebookEmail" name="facebookEmail" clearInput required></ion-input>
                </ion-item>
                <ion-item>
                    <ion-label i18n="@@credentials.model.password">Facebook password</ion-label>
                    <ion-input type="password" [(ngModel)]="facebookPassword" name="facebookPassword" clearInput required></ion-input>
                </ion-item>

                <ion-item>
                    <ion-label i18n="@@credentials.model.label">Label</ion-label>
                    <ion-input type="text" [(ngModel)]="label" name="label" clearInput required></ion-input>
                </ion-item>
            </form>
        </ion-content>
    `,
})
export class CredentialCreateComponent {

    readonly providers = Object.keys(Provider);

    provider: Provider;
    facebookEmail = '';
    facebookPassword = '';
    label = '';

    constructor(
        private loadingController: LoadingController,
        private navController: NavController,
        private toastController: ToastController,
        private credentialService: CredentialService,
    ) {
    }

    onCreate() {
        processLoading(this.loadingController,
            this.credentialService.registerFacebookCredential(this.provider, this.facebookEmail, this.facebookPassword, this.label).pipe(
                tap(() => this.navController.back()),
                catchError((error: HttpErrorResponse) =>
                    from(this.toastController.create({
                        color: 'danger',
                        message: (error.error as ApiError).message,
                        buttons: [
                            {text: 'Ok', role: 'cancel'}
                        ],
                        position: 'bottom'
                    })).pipe(
                        map((toast: HTMLIonToastElement) => toast.present()),
                        flatMap(() => throwError(error))))))
            .subscribe(noOp, noOp);
    }

}
