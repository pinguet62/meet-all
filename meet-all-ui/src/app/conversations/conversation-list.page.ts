import {Component} from '@angular/core';
import {LoadingController} from '@ionic/angular';
import {Conversation, Services} from '../services';
import {processLoading} from "../loading-controller.utils";

@Component({
    selector: 'app-conversation-list',
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
                <ion-item *ngFor="let conversation of conversations" [href]="'/tabs/(conversations:conversations/' + conversation.id + ')'">
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
export class ConversationListPage {

    conversations: Conversation[];

    constructor(loadingController: LoadingController, services: Services) {
        processLoading(loadingController, services.getConversations()).subscribe(
            it => this.conversations = it
        );
    }

}
