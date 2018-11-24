import {Component} from '@angular/core';
import {Location} from '@angular/common';
import {CredentialService, Provider} from './credential.service';
import {processLoading} from '../loading-controller.utils';
import {LoadingController} from '@ionic/angular';
import {tap} from 'rxjs/operators';

@Component({
    selector: 'app-credential-create',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-buttons slot="start">
                    <ion-button (click)="location.back()">
                        <ion-icon name="close"></ion-icon>
                        Back
                    </ion-button>
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
                        <ion-select-option value="TINDER">Tinder</ion-select-option>
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

    label = '';
    provider: Provider;
    credential = '';

    constructor(
        public location: Location,
        private loadingController: LoadingController,
        private service: CredentialService,
    ) {
    }

    onCreate() {
        processLoading(this.loadingController, this.service.registerCredential(this.provider, this.credential, this.label))
            .pipe(tap(() => this.location.back()))
            .subscribe();
    }

}
