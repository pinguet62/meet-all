import {Component} from '@angular/core';
import {LoadingController} from '@ionic/angular';
import {RefresherEventDetail} from '@ionic/core';
import {tap} from 'rxjs/operators';
import {processLoading} from '../../loading-controller.utils';
import {CredentialService, RegisteredCredential} from '../credential.service';
import {noOp} from '../../utils';

@Component({
    selector: 'app-credential-list',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-title i18n="@@credentials.title">Credentials</ion-title>
            </ion-toolbar>
        </ion-header>

        <ion-content>
            <ion-refresher slot="fixed" (ionRefresh)="onRefresh($event)" refreshingSpinner="circles" i18n-refreshingText="@@common.refreshing" refreshingText="Refreshing...">
                <ion-refresher-content></ion-refresher-content>
            </ion-refresher>
            <ion-list>
                <ion-item-sliding *ngFor="let credential of credentials">
                    <ion-item>
                        <ion-avatar>
                            <img [src]="'assets/provider/' + credential.provider + '.png'">
                        </ion-avatar>
                        <ion-label>{{credential.label}}</ion-label>
                        <ion-icon [name]="credential.ok ? 'checkmark' : 'close-circle'"></ion-icon>
                    </ion-item>
                    <ion-item-options>
                        <ion-item-option color="danger" (click)="onDelete(credential)" i18n="@@credentials.delete.button">Delete</ion-item-option>
                    </ion-item-options>
                </ion-item-sliding>
            </ion-list>

            <ion-fab vertical="bottom" horizontal="end" slot="fixed">
                <ion-fab-button routerLink="./create">
                    <ion-icon name="add"></ion-icon>
                </ion-fab-button>
            </ion-fab>
        </ion-content>
    `,
})
export class CredentialListComponent {

    credentials: RegisteredCredential[];

    constructor(
        private loadingController: LoadingController,
        private service: CredentialService
    ) {
        processLoading(this.loadingController,
            this.service.registeredCredentials()
                .pipe(tap(it => this.credentials = it)))
            .subscribe(noOp);
    }

    onDelete(registeredCredential: RegisteredCredential) {
        processLoading(this.loadingController,
            this.service.deleteCredential(registeredCredential.id)
                .pipe(tap(it => this.credentials.splice(this.credentials.findIndex(x => x.id === it.id), 1))))
            .subscribe(noOp);
    }

    onRefresh(e: Event) {
        const event = e as CustomEvent<RefresherEventDetail>;
        this.service.refreshRegisteredCredentials()
            .pipe(tap(event.detail.complete))
            .subscribe(noOp);
    }

}
