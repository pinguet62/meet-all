import {Component} from '@angular/core';
import {LoadingController} from '@ionic/angular';
import {RefresherEventDetail} from '@ionic/core';
import {tap} from "rxjs/operators";
import {processLoading} from '../../loading-controller.utils';
import {Conversation, ConversationsService} from '../conversations.service';

@Component({
    selector: 'app-conversation-list',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-title>Conversations</ion-title>
            </ion-toolbar>
        </ion-header>

        <ion-content>
            <ion-refresher slot="fixed" (ionRefresh)="onRefresh($event)" refreshingSpinner="circles" refreshingText="Refreshing...">
                <ion-refresher-content></ion-refresher-content>
            </ion-refresher>
            <ion-list>
                <ion-list-header>
                    <ion-label>Messages</ion-label>
                </ion-list-header>
                <ion-item *ngFor="let conversation of conversations"
                          [routerLink]="['/tabs/conversations/', conversation.id, conversation.profile.id]">
                    <ion-avatar>
                        <img [src]="conversation.profile.avatars[0]">
                    </ion-avatar>
                    <ion-label>
                        <h2>{{conversation.profile.name}}</h2>
                        <h3 *ngIf="conversation.lastMessage">{{conversation.lastMessage.text}}</h3>
                        <p *ngIf="conversation.lastMessage">{{conversation.lastMessage.date | date}}</p>
                    </ion-label>
                </ion-item>
            </ion-list>
        </ion-content>
    `
})
export class ConversationListPage {

    conversations: Conversation[];

    constructor(private loadingController: LoadingController, private service: ConversationsService) {
        processLoading(loadingController,
            this.service.getConversations()
                .pipe(tap(it => this.conversations = it))
        ).subscribe();
    }

    onRefresh(event: CustomEvent<RefresherEventDetail>) {
        this.service.getConversations()
            .pipe(tap(it => this.conversations = it))
            .pipe(tap(event.detail.complete))
            .subscribe();
    }

}
