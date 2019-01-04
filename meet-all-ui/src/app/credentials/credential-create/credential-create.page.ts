import {Component} from '@angular/core';
import {LoadingController, NavController} from '@ionic/angular';
import {tap} from 'rxjs/operators';
import {processLoading} from '../../loading-controller.utils';
import {CredentialService, Provider} from '../credential.service';

@Component({
    selector: 'app-credential-create',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-buttons slot="start">
                    <ion-back-button></ion-back-button>
                </ion-buttons>
                <ion-title>Credentials</ion-title>
                <ion-buttons slot="primary">
                    <ion-button (click)="onCreate()" [disabled]="!form.valid">
                        Create
                        <ion-icon name="checkmark"></ion-icon>
                    </ion-button>
                </ion-buttons>
            </ion-toolbar>
        </ion-header>

        <ion-content>
            <form #form="ngForm">
                <ion-item>
                    <ion-label>Label</ion-label>
                    <ion-input type="text" [(ngModel)]="label" name="label" clearInput required></ion-input>
                </ion-item>

                <ion-item>
                    <ion-label>Provider</ion-label>
                    <ion-select [(ngModel)]="provider" name="provider" required placeholder="Select one">
                        <ion-select-option *ngFor="let provider of providers" [value]="provider">{{provider}}</ion-select-option>
                    </ion-select>
                </ion-item>

                <ion-item>
                    <ion-label>Credential</ion-label>
                    <ion-input type="text" [(ngModel)]="credential" name="credential" clearInput required></ion-input>
                </ion-item>
            </form>
        </ion-content>
    `,
})
export class CredentialCreatePage {

    readonly providers = Object.keys(Provider);

    label = '';
    provider: Provider;
    credential = '';

    constructor(
        private loadingController: LoadingController,
        private navController: NavController,
        private service: CredentialService,
    ) {
    }

    onCreate() {
        processLoading(this.loadingController,
            this.service.registerCredential(this.provider, this.credential, this.label)
                .pipe(tap(() => this.navController.goBack()))
        ).subscribe();
    }

}