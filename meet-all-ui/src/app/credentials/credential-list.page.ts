import {Component} from '@angular/core';
import {RegisteredCredential, Services} from '../services';
import {LoadingController, ModalController} from '@ionic/angular';
import {EMPTY, from} from 'rxjs';
import {catchError, mapTo, mergeMap, tap} from 'rxjs/operators';

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

    constructor(
        loadingController: LoadingController,
        private modalController: ModalController,
        services: Services
    ) {
        from(loadingController.create())
            .pipe(tap((loader: HTMLIonLoadingElement) => loader.present()))
            .pipe(mergeMap(loader =>
                services.getRegisteredCredential()
                    .pipe(tap((it => this.credentials = it)))
                    .pipe(catchError(() => EMPTY))
                    .pipe(mapTo(loader))
            ))
            .pipe(mergeMap(loader => from(loader.dismiss())))
            .subscribe();
    }

}
