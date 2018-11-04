import {Component} from '@angular/core';
import {LoadingController} from '@ionic/angular';
import {Conversation, Services} from '../services';
import {EMPTY, from} from 'rxjs';
import {catchError, mapTo, mergeMap, tap} from 'rxjs/operators';

@Component({
    selector: 'app-conversations',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-title>Conversations</ion-title>
            </ion-toolbar>
        </ion-header>

        <ion-content>
            <ion-list>
                <ion-list-header>
                    <ion-label>Messages</ion-label>
                </ion-list-header>
                <ion-item *ngFor="let conversation of conversations">
                    <ion-avatar>
                        <img [src]="conversation.profile.avatars[0]">
                    </ion-avatar>
                    <ion-label>
                        <h2>{{conversation.profile.name}}</h2>
                        <h3>{{conversation.lastMessage.text}}</h3>
                        <p>{{conversation.lastMessage.date | date}}</p>
                    </ion-label>
                </ion-item>
            </ion-list>
        </ion-content>
    `
})
export class ConversationsPage {

    conversations: Conversation[];

    constructor(loadingController: LoadingController, services: Services) {
        from(loadingController.create())
            .pipe(tap((loader: HTMLIonLoadingElement) => loader.present()))
            .pipe(mergeMap(loader =>
                services.list()
                    .pipe(tap((it => this.conversations = it)))
                    .pipe(catchError(() => EMPTY))
                    .pipe(mapTo(loader))
            ))
            .pipe(mergeMap(loader => from(loader.dismiss())))
            .subscribe();
    }

}
