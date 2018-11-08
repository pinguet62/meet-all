import {Component} from '@angular/core';
import {Message, Services} from '../services';
import {LoadingController} from '@ionic/angular';
import {EMPTY, from} from 'rxjs';
import {catchError, mapTo, mergeMap, tap} from 'rxjs/operators';
import {Location} from '@angular/common';

@Component({
    selector: 'app-conversation-messages',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-buttons slot="start">
                    <ion-button (click)="location.back()">
                        <ion-icon name="close"></ion-icon>
                        Back
                    </ion-button>
                </ion-buttons>
                <ion-title>Messages</ion-title>
            </ion-toolbar>
        </ion-header>

        <ion-content>
            <ion-list>
                <ion-item *ngFor="let message of messages">

                </ion-item>
            </ion-list>
        </ion-content>
    `
})
export class ConversationMessagesPage {

    messages: Message[] = [];

    constructor(
        public location: Location,
        loadingController: LoadingController,
        services: Services
    ) {
        from(loadingController.create())
            .pipe(tap((loader: HTMLIonLoadingElement) => loader.present()))
            .pipe(mergeMap(loader =>
                services.getMessagesByConversation(null)
                    .pipe(tap((it => this.messages = it)))
                    .pipe(catchError(() => EMPTY))
                    .pipe(mapTo(loader))
            ))
            .pipe(mergeMap(loader => from(loader.dismiss())))
            .subscribe();
    }

}
