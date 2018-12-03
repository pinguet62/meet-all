import {Component} from '@angular/core';
import {LoadingController} from '@ionic/angular';
import {processLoading} from '../loading-controller.utils';
import {CredentialService, Provider, RegisteredCredential} from './credential.service';

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
                        <ion-item-option color="danger" (click)="onDelete(credential.id)">Delete</ion-item-option>
                    </ion-item-options>
                </ion-item-sliding>
            </ion-list>

            <ion-fab vertical="bottom" horizontal="end" slot="fixed">
                <ion-fab-button>
                    <ion-icon name="add"></ion-icon>
                </ion-fab-button>
                <ion-fab-list side="top">
                    <ion-fab-button *ngFor="let provider of providers" href="/tabs/(credentials:credentials/create)">
                        <ion-img [src]="'assets/provider/' + provider + '.png'"></ion-img>
                    </ion-fab-button>
                </ion-fab-list>
            </ion-fab>
        </ion-content>
    `,
})
export class CredentialListPage {

    readonly providers = Object.keys(Provider);

    credentials: RegisteredCredential[];

    constructor(
        private loadingController: LoadingController,
        private service: CredentialService
    ) {
        this.refresh();
    }

    onDelete(id: number) {
        processLoading(this.loadingController, this.service.deleteCredential(id))
            .subscribe(() => this.refresh());
    }

    private refresh() {
        processLoading(this.loadingController, this.service.getRegisteredCredential())
            .subscribe(it => this.credentials = it);
    }

}
