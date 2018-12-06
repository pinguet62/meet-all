import {Component} from '@angular/core';
import {LoadingController} from '@ionic/angular';
import {Location} from '@angular/common';
import {processLoading} from '../loading-controller.utils';
import {ConversationsService, Message} from './conversations.service';
import {tap} from "rxjs/operators";

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
            <ion-list lines="none">
                <ion-item *ngFor="let message of messages">
                    <ion-avatar *ngIf="!message.sent">
                        <ion-img src="assets/provider/TINDER.png"></ion-img>
                    </ion-avatar>
                    <ion-text [slot]="message.sent ? 'end' : ''" [ngClass]="['message', message.sent ? 'sent' : 'received']">{{message.text}}</ion-text>
                </ion-item>
            </ion-list>
        </ion-content>
    `,
    styles: [
            `
            .message {
                max-width: 75%;
                white-space: pre-wrap;
            }

            .sent {
                background-color: rgb(0, 162, 216)
            }

            .received {
                background-color: rgb(225, 225, 225);
            }
        `
    ]
})
export class ConversationMessagesPage {

    messages: Message[] = [];

    constructor(
        public location: Location,
        loadingController: LoadingController,
        service: ConversationsService
    ) {
        processLoading(loadingController,
            service.getMessagesByConversation(null)
                .pipe(tap(it => this.messages = it))
        ).subscribe();
    }

}
