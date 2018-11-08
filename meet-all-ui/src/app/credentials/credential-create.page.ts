import {Component} from '@angular/core';
import {Location} from '@angular/common';
import {Provider, Services} from '../services';

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
                    <ion-button>
                        Create
                        <ion-icon name="checkmark"></ion-icon>
                    </ion-button>
                </ion-buttons>
            </ion-toolbar>
        </ion-header>

        <ion-content>
            <ion-item>
                <ion-label>Label</ion-label>
                <ion-input type="text" [(ngModel)]="label" clearInput required></ion-input>
            </ion-item>

            <ion-item>
                <ion-label>Provider</ion-label>
                <ion-select [(ngModel)]="provider" required placeholder="Select one">
                    <ion-select-option value="TINDER">Tinder</ion-select-option>
                </ion-select>
            </ion-item>

            <ion-item>
                <ion-label>Secret</ion-label>
                <ion-input type="text" [(ngModel)]="secret" clearInput required></ion-input>
            </ion-item>
        </ion-content>
    `,
})
export class CredentialCreatePage {

    label = '';
    provider: Provider;
    secret = '';

    constructor(public location: Location, private services: Services) {
    }

}
