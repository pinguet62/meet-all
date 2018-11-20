import {Component} from '@angular/core';
import {RegisteredCredential, Services} from '../services';
import {LoadingController} from '@ionic/angular';
import {processLoading} from "../loading-controller.utils";

@Component({
    selector: 'app-credential-list',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-title>Credentials</ion-title>
            </ion-toolbar>
        </ion-header>

        <ion-content>
            <ion-list>
                <ion-item-sliding *ngFor="let credential of credentials">
                    <ion-item>
                        <ion-avatar>
                            <img [src]="'assets/provider/' + credential.provider + '.png'">
                        </ion-avatar>
                        <ion-label>{{credential.label}}</ion-label>
                    </ion-item>
                    <ion-item-options>
                        <ion-item-option color="danger">Delete</ion-item-option>
                    </ion-item-options>
                </ion-item-sliding>
            </ion-list>

            <ion-fab vertical="bottom" horizontal="end" slot="fixed">
                <ion-fab-button>
                    <ion-icon name="add"></ion-icon>
                </ion-fab-button>
                <ion-fab-list side="top">
                    <ion-fab-button href="/tabs/(credentials:credentials/create)">
                        <ion-img src="assets/provider/TINDER.png"></ion-img>
                    </ion-fab-button>
                </ion-fab-list>
            </ion-fab>
        </ion-content>
    `,
})
export class CredentialListPage {

    credentials: RegisteredCredential[];

    constructor(loadingController: LoadingController, services: Services) {
        processLoading(loadingController, services.getRegisteredCredential()).subscribe(
            it => this.credentials = it
        );
    }

}
