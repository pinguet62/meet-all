import {Component} from '@angular/core';
import {LoadingController} from '@ionic/angular';
import {RefresherEventDetail} from '@ionic/core';
import {tap} from 'rxjs/operators';
import {processLoading} from '../../loading-controller.utils';
import {Conversation, ConversationsService} from '../conversations.service';

@Component({
    selector: 'app-conversation-list',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-title i18n="@@conversations.title">Conversations</ion-title>
            </ion-toolbar>
        </ion-header>

        <ion-content>
            <ion-refresher slot="fixed" (ionRefresh)="onRefresh($event)" refreshingSpinner="circles" i18n-refreshingText="@@common.refreshing" refreshingText="Refreshing...">
                <ion-refresher-content></ion-refresher-content>
            </ion-refresher>
            <ion-list>
                <ion-list-header>
                    <ion-label i18n="@@conversations.messages">Messages</ion-label>
                </ion-list-header>
                <ion-item *ngFor="let conversation of conversations"
                          [routerLink]="['/tabs/conversations/', conversation.id, conversation.profile.id]">
                    <ion-avatar>
                        <img [appProxifiedSrc]="conversation.profile.avatar">
                        <app-badge-icon *ngIf="conversation.lastMessage != null"
                                        [name]="conversation.lastMessage.sent ? 'arrow-undo-outline' : 'arrow-redo-outline'"
                                        [color]="conversation.lastMessage.sent ? 'success' : 'danger'"></app-badge-icon>
                    </ion-avatar>
                    <ion-label>
                        <h2>{{conversation.profile.name}}</h2>
                        <h3 *ngIf="conversation.lastMessage">{{conversation.lastMessage.text}}</h3>
                        <p *ngIf="conversation.lastMessage">{{conversation.lastMessage.date | date}}</p>
                    </ion-label>
                </ion-item>
            </ion-list>
        </ion-content>
    `,
})
export class ConversationListComponent {

    conversations: Conversation[];

    constructor(private loadingController: LoadingController, private service: ConversationsService) {
        processLoading(loadingController,
            this.service.getConversations()
                .pipe(tap(it => this.conversations = it))
        ).subscribe();
    }

    onRefresh(e: Event) {
        const event = e as CustomEvent<RefresherEventDetail>; // TODO best typing
        this.service.getConversations()
            .pipe(tap(it => this.conversations = it))
            .pipe(tap(event.detail.complete))
            .subscribe();
    }

}
